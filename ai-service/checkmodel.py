import os
import google.generativeai as genai
from dotenv import load_dotenv

# Đọc API Key từ file .env
load_dotenv()
api_key = os.getenv("GEMINI_API_KEY")
genai.configure(api_key=api_key)

print("Đang kết nối với Google để lấy danh sách Model của bạn...\n")

# Lặp qua tất cả các model mà API Key này có quyền truy cập
print("👇 ĐÂY LÀ NHỮNG MODEL BẠN CÓ THỂ DÙNG (Cho tính năng Chat) 👇")
count = 0
for m in genai.list_models():
    # Chỉ lấy những model hỗ trợ tạo văn bản (generateContent)
    if 'generateContent' in m.supported_generation_methods:
        print(f"- Tên copy vào code: {m.name}")
        count += 1

print(f"\nTổng cộng tìm thấy {count} model khả dụng.")