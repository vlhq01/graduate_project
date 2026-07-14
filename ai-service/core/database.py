import os

from dotenv import load_dotenv
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, declarative_base

# Đọc file .env
load_dotenv()

# Lấy URL kết nối từ file .env
SQLALCHEMY_DATABASE_URL = os.getenv("DATABASE_URL")

# Tạo Engine kết nối (Tương đương DataSource bên Java)
engine = create_engine(SQLALCHEMY_DATABASE_URL)

# Tạo Session (Tương đương EntityManager)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

# Base class để các Entity kế thừa
Base = declarative_base()


# Hàm này giống kiểu Dependency Injection để cấp Session cho các API
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
