package com.example.techadvisor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.OffsetDateTime;

@Entity
@Table(name = "users")
@Data
public class User {

    @Id
    private String id;

    private String email;
    private String phoneNumber;
    private String name;
    private String avatarUrl;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime createdAt;
}
