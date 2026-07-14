package com.example.techadvisor.controller;

import com.example.techadvisor.dto.SaveFcmTokenDTO;
import com.example.techadvisor.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @PostMapping("/fcm-token")
    public ResponseEntity<Void> saveFcmToken(@RequestBody SaveFcmTokenDTO dto) {
        notificationService.saveToken(getCurrentUserId(), dto.getToken());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/test")
    public ResponseEntity<Void> sendTestNotification() {
        notificationService.sendToUser(
                getCurrentUserId(),
                "TechAdvisor",
                "Bạn có gợi ý sản phẩm mới phù hợp với nhu cầu của mình.",
                Map.of("type", "RECOMMENDATION")
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/testcustom")
    public ResponseEntity<Void> sendTestCustomNotification() {
        String userId = getCurrentUserId();
        notificationService.sendRecommendationNotificationToUser(
                userId,
                "laptop_12",
                "AI Gợi ý cho bạn",
                "MacBook Air M4 đang giảm 13% - phù hợp với nhu cầu chỉnh sửa video của bạn!",
                "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcT-CqMdL5ZdOOLFC00oCIVpxyDMcGeikTpLfuJgA2tu-w&s=10"
        );
        return ResponseEntity.ok().build();
    }
}