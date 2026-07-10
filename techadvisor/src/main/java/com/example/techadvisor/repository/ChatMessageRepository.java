package com.example.techadvisor.repository;

import com.example.techadvisor.entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    Page<ChatMessage> findByUserIdOrderByCreatedAtDesc(String userId, Pageable pageable);

    List<ChatMessage> findTop10ByUserIdOrderByCreatedAtDesc(String userId);

    Optional<ChatMessage> findFirstByUserIdAndSenderTypeOrderByCreatedAtDesc(String userId, String senderType);
}
