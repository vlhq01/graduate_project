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

    @PostMapping(value = "/sync", produces = "application/json")
    public ResponseEntity<?> syncUser(@RequestBody UserSyncDTO userSyncDTO) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Missing or invalid token");
        }

        String uid = (String) auth.getPrincipal();

        User user = userService.syncUser(uid, userSyncDTO);

        return ResponseEntity.ok(user);
    }
}
