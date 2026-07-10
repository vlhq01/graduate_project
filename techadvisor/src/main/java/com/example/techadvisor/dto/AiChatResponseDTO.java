package com.example.techadvisor.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class AiChatResponseDTO {
    @JsonProperty("user_message")
    private String userMessage;

    @JsonProperty("bot_reply")
    private String botReply;

    @JsonProperty("suggested_products_ids")
    private List<String> suggestedProductIds;

    private String intent;
}
