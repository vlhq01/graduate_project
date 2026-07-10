package com.example.techadvisor.service;

import com.example.techadvisor.dto.UserSyncDTO;
import com.example.techadvisor.entity.User;
import com.example.techadvisor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User syncUser(String uid, UserSyncDTO dto) {
        // Tìm user trong DB bằng UID
        return userRepository.findById(uid).orElseGet(() -> {
            // orElseGet: Nếu KHÔNG TÌM THẤY thì chạy đoạn code dưới đây để tạo mới
            User newUser = new User();
            newUser.setId(uid);
            newUser.setEmail(dto.getEmail());
            newUser.setName(dto.getName());
            newUser.setAvatarUrl(dto.getAvatarUrl());
            newUser.setPhoneNumber(dto.getPhoneNumber());
            newUser.setCreatedAt(OffsetDateTime.now());

            // Lưu vào DB và trả về
            return userRepository.save(newUser);
        });
    }
}
