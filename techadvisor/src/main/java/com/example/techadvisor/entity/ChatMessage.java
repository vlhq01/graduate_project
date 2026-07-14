package com.example.techadvisor.entity;

import com.example.techadvisor.utils.StringListConverter;
import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "chat_messages")
@Data
public class ChatMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "sender_type", nullable = false)
    private String senderType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @Convert(converter = StringListConverter.class)
    @Column(name = "suggested_products", columnDefinition = "TEXT")
    private List<String> suggestedProducts;
}
