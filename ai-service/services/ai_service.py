from sentence_transformers import SentenceTransformer

print("Đang tải AI Model... (Lần đầu sẽ mất khoảng 1-2 phút để tải model về máy)")
# Dùng model này vì nó rất nhẹ, chạy siêu nhanh và tạo ra vector 384 chiều (khớp với DB của bạn)
model = SentenceTransformer('all-MiniLM-L6-v2')
print("Tải AI Model thành công! 🚀")

def get_embedding(text: str) -> list[float]:
    """
    Hàm này nhận vào một đoạn text, đưa qua model AI và trả về một mảng số (vector).
    """
    vector = model.encode(text)
    return vector.tolist()