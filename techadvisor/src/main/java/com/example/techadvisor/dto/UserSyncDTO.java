package com.example.techadvisor.dto;

import lombok.Data;

@Data
public class UserSyncDTO {
    // LƯU Ý: Không có trường 'id' ở đây.
    // Vì 'id' (UID) ta sẽ lấy trực tiếp từ Token để đảm bảo hacker không thể làm giả ID.
    private String email;
    private String name;
    private String avatarUrl;
    private String phoneNumber;
}