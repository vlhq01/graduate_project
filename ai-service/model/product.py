from pgvector.sqlalchemy import Vector
from sqlalchemy import Column, String, JSON

from core.database import Base


class Product(Base):
    __tablename__ = "products"

    id = Column(String, primary_key=True, index=True)
    name = Column(String)
    brand = Column(String)
    category = Column(String)

    specs = Column(JSON)

    embedding = Column(Vector(384))
