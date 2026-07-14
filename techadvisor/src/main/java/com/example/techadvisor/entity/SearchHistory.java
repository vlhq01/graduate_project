package com.example.techadvisor.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "search_history")
@Data
public class SearchHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(nullable = false)
    private String query;

    @Column(name = "created_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}
