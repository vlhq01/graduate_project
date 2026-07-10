package com.example.techadvisor.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "workspace_items")
@Data
public class WorkspaceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "product_id", nullable = false)
    private String productId;

    @Column(name = "added_at", columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime addedAt;
}
