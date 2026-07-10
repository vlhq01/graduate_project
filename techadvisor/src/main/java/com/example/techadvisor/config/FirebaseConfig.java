package com.example.techadvisor.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FirebaseConfig {

    @Bean // Dùng @Bean thay cho @PostConstruct để đảm bảo nó được tạo ra và quản lý bởi Spring
    public FirebaseApp firebaseApp() throws IOException {
        // Lấy file service account từ thư mục resources
        InputStream serviceAccount = new ClassPathResource("firebase-service-account.json").getInputStream();

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        // Kiểm tra xem app đã được khởi tạo chưa để tránh lỗi
        if (FirebaseApp.getApps().isEmpty()) {
            System.out.println("✅✅✅ CHECKPOINT 1: ĐANG KHỞI TẠO FIREBASE...");
            return FirebaseApp.initializeApp(options);
        } else {
            return FirebaseApp.getInstance();
        }
    }
}
