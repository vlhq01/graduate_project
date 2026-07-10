package com.example.techadvisor.service;

import com.example.techadvisor.entity.UserDeviceToken;
import com.example.techadvisor.repository.UserDeviceTokenRepository;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private UserDeviceTokenRepository tokenRepository;

    public void saveToken(String userId, String fcmToken) {
        UserDeviceToken token = tokenRepository.findByFcmToken(fcmToken)
                .orElseGet(UserDeviceToken::new);

        token.setUserId(userId);
        token.setFcmToken(fcmToken);
        token.setUpdatedAt(OffsetDateTime.now());

        tokenRepository.save(token);
    }

    public void sendToUser(String userId, String title, String body, Map<String, String> data) {
        var tokens = tokenRepository.findByUserId(userId);

        for (UserDeviceToken token : tokens) {
            try {
                Message.Builder builder = Message.builder()
                        .setToken(token.getFcmToken())
                        .setNotification(Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build());

                if (data != null) {
                    builder.putAllData(data);
                }

                FirebaseMessaging.getInstance().send(builder.build());
            } catch (Exception e) {
                System.err.println("FCM send failed: " + e.getMessage());
            }
        }
    }

    public void sendRecommendationNotification(
            String fcmToken,
            String productId,
            String title,
            String body,
            String imageUrl
    ) throws Exception {
        Message message = Message.builder()
                .setToken(fcmToken)
                .putData("type", "AI_PICK")
                .putData("productId", productId)
                .putData("title", title)
                .putData("body", body)
                .putData("imageUrl", imageUrl != null ? imageUrl : "")
                .build();

        FirebaseMessaging.getInstance().send(message);
    }


    public void sendRecommendationNotificationToUser(
            String userId,
            String productId,
            String title,
            String body,
            String imageUrl
    ) {
        var tokens = tokenRepository.findByUserId(userId);

        for (UserDeviceToken token : tokens) {
            try {
                sendRecommendationNotification(
                        token.getFcmToken(),
                        productId,
                        title,
                        body,
                        imageUrl
                );
            } catch (Exception e) {
                System.err.println("FCM send failed: " + e.getMessage());
            }
        }
    }
}