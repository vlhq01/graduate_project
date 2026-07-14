import os

from dotenv import load_dotenv
from groq import Groq

load_dotenv()
GROQ_API_KEY = os.getenv("GROQ_API_KEY")

client = Groq(api_key=GROQ_API_KEY)


def generate_chat_response(
        user_message: str,
        product_context: str,
        history: list[dict] | None = None,
        context: dict | None = None
) -> str:
    history = history or []
    context = context or {}

    history_text = "\n".join(
        f"{item.get('role')}: {item.get('content')}"
        for item in history[-8:]
    )

    prompt = f"""
Bạn là nhân viên tư vấn sản phẩm công nghệ của TechAdvisor.

Nhiệm vụ:
- Hiểu câu hỏi hiện tại dựa trên lịch sử hội thoại.
- Chỉ tư vấn dựa trên danh sách sản phẩm được cung cấp.
- Nếu người dùng hỏi tiếp kiểu "cái đó", "rẻ hơn", "đổi sang Nvidia", hãy dùng lịch sử chat để hiểu ngữ cảnh.
- Giải thích ngắn gọn vì sao sản phẩm phù hợp.
- Nếu thiếu dữ liệu để kết luận, nói rõ và hỏi lại đúng thông tin cần thiết.
- Không bịa sản phẩm ngoài danh sách.

LỊCH SỬ CHAT:
{history_text}

NGỮ CẢNH HỆ THỐNG:
{context}

SẢN PHẨM LIÊN QUAN:
{product_context}
"""

    try:
        chat_completion = client.chat.completions.create(
            messages=[
                {"role": "system", "content": prompt},
                {"role": "user", "content": user_message}
            ],
            model="openai/gpt-oss-120b",
            temperature=0.4,
        )
        return chat_completion.choices[0].message.content
    except Exception as e:
        return f"Xin lỗi, hệ thống AI đang gặp sự cố: {str(e)}"
