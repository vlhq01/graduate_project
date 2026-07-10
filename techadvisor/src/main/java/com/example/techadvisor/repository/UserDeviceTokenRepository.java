package com.example.techadvisor.repository;

import com.example.techadvisor.entity.UserDeviceToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserDeviceTokenRepository extends JpaRepository<UserDeviceToken, Long> {
    Optional<UserDeviceToken> findByFcmToken(String fcmToken);
    List<UserDeviceToken> findByUserId(String userId);
}