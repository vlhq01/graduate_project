package com.example.techadvisor.controller;

import com.example.techadvisor.dto.UserSyncDTO;
import com.example.techadvisor.entity.User;
import com.example.techadvisor.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    // API này dùng POST vì Android sẽ gửi dữ liệu (body) lên
    @PostMapping(value = "/sync", produces = "application/json")
    public ResponseEntity<?> syncUser(@RequestBody UserSyncDTO userSyncDTO) {

        // 1. Lấy thông tin chứng minh nhân dân (UID) từ anh bảo vệ Spring Security
        // (Nhờ cái FirebaseTokenFilter lúc nãy mà anh bảo vệ đã có thông tin này)
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem có đưa thẻ (token) không
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        // Lấy cái UID ra
        String uid = (String) auth.getPrincipal();

        // 2. Gọi Service để đồng bộ User
        User user = userService.syncUser(uid, userSyncDTO);

        // 3. Trả về thông tin User
        return ResponseEntity.ok(user);
    }
}
