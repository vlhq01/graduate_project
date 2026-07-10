from fastapi import FastAPI, Depends, HTTPException
from sqlalchemy.orm import Session

from core.database import get_db, engine
from model import product
from model.product import Product
from services.ai_service import get_embedding

from services.chatservice import generate_chat_response
import uvicorn

from pydantic import BaseModel
from sqlalchemy import text
from fastapi import Depends

from fastapi import Path

from typing import Any



# Đây chính là DTO hứng dữ liệu từ Spring Boot gửi sang
class SearchRequest(BaseModel):
    query: str  # Từ khóa user gõ (VD: "iphone 16 128gb")
    limit: int = 10
    category: str = None
app = FastAPI(title="TechAdvisor AI Service")


@app.post("/api/search")
def search_products(req: SearchRequest, db: Session = Depends(get_db)):
    query_vector = get_embedding(req.query)

    # Tạo câu WHERE động dựa vào category
    category_filter = ""
    params = {"query": req.query, "vec": str(query_vector), "limit": req.limit}

    if req.category:
        # Nếu có chọn Tab Category, bắt buộc DB chỉ được lọc những sản phẩm đó
        category_filter = "WHERE category = :cat"
        params["cat"] = req.category

    sql_query = text(f"""
        SELECT id, name
        FROM products
        {category_filter}  -- Chèn điều kiện lọc vào đây
        ORDER BY (CASE WHEN name ILIKE '%' || :query || '%' THEN 1.0 ELSE 0.0 END) * 0.6 
                 + (1.0 - (embedding <=> :vec)) * 0.4 DESC
        LIMIT :limit
    """)

    result = db.execute(sql_query, params).fetchall()
    return [row.id for row in result]




@app.get("/api/health")
def health_check(db: Session = Depends(get_db)):
    try:
        # Thử chạy 1 lệnh SQL đếm số lượng product
        product_count = db.query(product.Product).count()
        return {
            "status": "ok",
            "database": "Kết nối PostgreSQL THÀNH CÔNG! 🎉",
            "total_products": product_count
        }
    except Exception as e:
        return {
            "status": "error",
            "database": f"Lỗi kết nối: {str(e)}"
        }


@app.post("/api/admin/sync-embeddings")
def sync_product_embeddings(db: Session = Depends(get_db)):
    try:
        # Bắt buộc phải có flush=True để nó xuyên qua buffer của Docker, hiện log ngay lập tức
        print("🚀 BẮT ĐẦU CHẠY SYNC FUNCTION...", flush=True)

        # MẸO: Thêm .limit(10) để test thử 10 sản phẩm trước. Đừng nhét 3000 cái 1 lúc nó sẽ treo!
        products = db.query(Product).filter(Product.embedding.is_(None)).all()

        if not products:
            print("✅ Không có sản phẩm nào cần nhúng.", flush=True)
            return {"message": "Tất cả sản phẩm đã được nhúng vector rồi!"}

        print(f"📦 Tìm thấy {len(products)} sản phẩm cần tạo vector. Đang xử lý...", flush=True)

        count = 0
        for p in products:
            text_to_embed = f"Sản phẩm {p.name}, thương hiệu {p.brand}, danh mục {p.category}. Thông số: {str(p.specs)}"

            vector = get_embedding(text_to_embed)

            if hasattr(vector, "tolist"):
                vector = vector.tolist()

            p.embedding = vector
            count += 1
            print(f"-> Đã tạo vector xong cho: {p.name}", flush=True)  # In ra từng cái một

        db.commit()

        print("🎉 KẾT THÚC SYNC FUNCTION THÀNH CÔNG!", flush=True)
        return {"message": f"Thành công! Đã nhúng vector cho {count} sản phẩm."}

    except Exception as e:
        error_msg = f"Lỗi Python: {str(e)}"
        print(f"❌ LỖI RỒI: {error_msg}", flush=True)
        raise HTTPException(status_code=500, detail=error_msg)


class RecommendRequest(BaseModel):
    query: str  # Ví dụ: "Điện thoại pin trâu giá rẻ"
    limit: int = 5


@app.post("/api/recommend")
def get_recommendations(req: RecommendRequest, db: Session = Depends(get_db)):
    # 1. Biến câu yêu cầu của user thành Vector
    query_vector = get_embedding(req.query)

    # 2. Ép kiểu vector thành chuỗi để đưa vào SQL
    vector_str = str(query_vector)

    # 3. Câu lệnh SQL ma thuật của pgvector (Dùng toán tử <=> để tính khoảng cách Cosine)
    sql_query = text("""
        SELECT id, name, brand, category, price,
               1 - (embedding <=> :vec) AS similarity_score
        FROM products
        ORDER BY embedding <=> :vec
        LIMIT :limit
    """)

    # 4. Thực thi truy vấn
    result = db.execute(sql_query, {"vec": vector_str, "limit": req.limit}).fetchall()

    # 5. Format kết quả trả về
    recommendations = []
    for row in result:
        recommendations.append({
            "id": row.id,
            "name": row.name,
            "brand": row.brand,
            "price": row.price,
            "match_score": round(row.similarity_score * 100, 2)  # Tính ra phần trăm giống nhau
        })

    return {
        "query": req.query,
        "recommendations": recommendations
    }


class ChatHistoryItem(BaseModel):
    role: str
    content: str

class ChatRequest(BaseModel):
    message: str
    history: list[ChatHistoryItem] = []
    context: dict[str, Any] = {}


CATEGORY_KEYWORDS = {
    "cpu": ["cpu", "processor", "chip", "i3", "i5", "i7", "ryzen"],
    "gpu": ["gpu", "card màn hình", "vga", "rtx", "gtx", "radeon"],
    "laptop": ["laptop", "máy tính xách tay", "notebook"],
    "phone": ["phone", "điện thoại", "iphone", "samsung"],
    "ram": ["ram", "memory"],
    "motherboard": ["main", "mainboard", "motherboard"],
    "psu": ["nguồn", "psu", "power supply"],
    "monitor": ["màn hình", "monitor"],
    "keyboard": ["bàn phím", "keyboard"],
    "mouse": ["chuột", "mouse"],
    "headphone": ["tai nghe", "headphone", "earbud"],
}

def detect_category(message: str) -> str | None:
    text = message.lower()
    for category, keywords in CATEGORY_KEYWORDS.items():
        if any(keyword in text for keyword in keywords):
            return category
    return None

@app.post("/api/chat")
def chat_with_ai(req: ChatRequest, db: Session = Depends(get_db)):
    category = detect_category(req.message)
    query_vector = get_embedding(req.message)

    category_filter = ""
    params = {
        "query": req.message,
        "vec": str(query_vector),
        "limit": 8
    }

    if category:
        category_filter = "AND category = :category"
        params["category"] = category

    sql_query = text(f"""
        SELECT id, name, brand, category, price, specs,
               (
                   (CASE WHEN name ILIKE '%' || :query || '%' THEN 1.0 ELSE 0.0 END) * 0.35
                   + (1.0 - (embedding <=> :vec)) * 0.50
                   + (COALESCE(rating, 0) / 5.0) * 0.10
                   + (CASE WHEN stock > 0 THEN 0.05 ELSE 0 END)
               ) AS match_score
        FROM products
        WHERE embedding IS NOT NULL
        {category_filter}
        ORDER BY match_score DESC
        LIMIT :limit
    """)

    result = db.execute(sql_query, params).fetchall()

    context_list = []
    product_ids = []

    for row in result:
        context_list.append(
            f"- ID: {row.id} | {row.name} ({row.brand}) | "
            f"Category: {row.category} | Price: {row.price} | Specs: {row.specs}"
        )
        product_ids.append(row.id)

    product_context = "\n".join(context_list)

    ai_reply = generate_chat_response(
        user_message=req.message,
        product_context=product_context,
        history=[item.model_dump() for item in req.history],
        context=req.context
    )

    return {
        "user_message": req.message,
        "intent": "find_product" if category else "general_advice",
        "bot_reply": ai_reply,
        "suggested_products_ids": product_ids
    }


@app.get("/api/recommend/similar/{product_id}")
def get_similar_products(
        product_id: str = Path(..., description="ID của sản phẩm đang xem"),
        limit: int = 4,
        db: Session = Depends(get_db)
):
    # MA THUẬT SQL NẰM Ở ĐÂY:
    # 1. Lấy vector của chính cái 'product_id' đó ra.
    # 2. Tính khoảng cách Cosine (<=>) giữa vector đó với toàn bộ sản phẩm trong bảng.
    # 3. Loại trừ chính nó ra khỏi kết quả (id != :p_id).
    sql_query = text("""
        SELECT id 
        FROM products 
        WHERE id != :p_id 
        ORDER BY embedding <=> (SELECT embedding FROM products WHERE id = :p_id) 
        LIMIT :limit
    """)

    # Thực thi Query
    result = db.execute(sql_query, {"p_id": product_id, "limit": limit}).fetchall()

    # Trả về mảng ID cho Spring Boot (VD: ["phone_1", "laptop_5", ...])
    return [row.id for row in result]


if __name__ == "__main__":
    uvicorn.run("main:app", host="127.0.0.1", port=8000, reload=True)
