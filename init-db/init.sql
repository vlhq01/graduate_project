-- 1. Bật tính năng Vector cho DB
CREATE EXTENSION IF NOT EXISTS vector;

-- 2. Tạo trước bảng products với khóa chính và cột embedding
CREATE TABLE IF NOT EXISTS products (
    id VARCHAR(255) PRIMARY KEY,
    embedding vector(384)
);