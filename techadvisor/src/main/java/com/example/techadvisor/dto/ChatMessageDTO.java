package com.example.techadvisor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessageDTO {
    private Long id;
    private String senderType; // "USER" hoặc "AI"
    private String content;
    private String createdAt; // Trả về dạng chuỗi ISO 8601 để Android dễ hiển thị
    private List<ProductDTO> suggestedProducts;
}