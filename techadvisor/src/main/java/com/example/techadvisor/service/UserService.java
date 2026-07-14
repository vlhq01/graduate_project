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
        return userRepository.findById(uid).orElseGet(() -> {
            User newUser = new User();
            newUser.setId(uid);
            newUser.setEmail(dto.getEmail());
            newUser.setName(dto.getName());
            newUser.setAvatarUrl(dto.getAvatarUrl());
            newUser.setPhoneNumber(dto.getPhoneNumber());
            newUser.setCreatedAt(OffsetDateTime.now());

            return userRepository.save(newUser);
        });
    }
}
