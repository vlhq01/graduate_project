package com.example.techadvisor.dto;

import lombok.Data;

import java.util.List;

@Data
public class ChatMessageDTO {
    private Long id;
    private String senderType;
    private String content;
    private String createdAt;
    private List<ProductDTO> suggestedProducts;
}