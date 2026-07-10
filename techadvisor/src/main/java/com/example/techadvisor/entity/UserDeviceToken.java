package com.example.techadvisor.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Table(name = "user_device_tokens")
@Data
public class UserDeviceToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;

    @Column(length = 512, unique = true, nullable = false)
    private String fcmToken;

    private OffsetDateTime updatedAt;
}