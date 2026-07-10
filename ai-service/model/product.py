from sqlalchemy import Column, String, JSON
from pgvector.sqlalchemy import Vector
from core.database import Base


class Product(Base):
    __tablename__ = "products"  # Tên bảng trong Postgres của bạn

    id = Column(String, primary_key=True, index=True)
    name = Column(String)
    brand = Column(String)
    category = Column(String)

    # Cột chứa thông số kĩ thuật (JSON) để AI đọc
    specs = Column(JSON)

    # ĐÂY LÀ CỘT QUAN TRỌNG NHẤT: Lưu Vector 384 chiều
    embedding = Column(Vector(384))