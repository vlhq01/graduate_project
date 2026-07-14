package com.example.techadvisor.dto;

import lombok.Data;

@Data
public class UserSyncDTO {
    private String email;
    private String name;
    private String avatarUrl;
    private String phoneNumber;
}