package com.example.techadvisor.dto;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class AiChatRequestDTO {
    private String message;
    private List<ChatHistoryItemDTO> history = new ArrayList<>();
    private Map<String, Object> context = new HashMap<>();

    public AiChatRequestDTO(String message) {
        this.message = message;
    }

    public AiChatRequestDTO(String message, List<ChatHistoryItemDTO> history, Map<String, Object> context) {
        this.message = message;
        this.history = history;
        this.context = context;
    }
}
