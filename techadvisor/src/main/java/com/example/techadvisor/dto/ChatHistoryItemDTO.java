package com.example.techadvisor.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistoryItemDTO {
    private String role;    // "user" or "assistant"
    private String content;
}
