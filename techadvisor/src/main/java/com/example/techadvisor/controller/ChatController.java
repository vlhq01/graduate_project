package com.example.techadvisor.controller;

import com.example.techadvisor.dto.ChatMessageDTO;
import com.example.techadvisor.dto.ChatRequestDTO;
import com.example.techadvisor.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    private ChatService chatService;

    // Hàm lấy ID từ Token
    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    // API 1: Lấy lịch sử chat (Để Android vuốt lên tải thêm)
    // Link gọi: GET /api/chat/history?page=0&size=20
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String userId = getCurrentUserId();
        List<ChatMessageDTO> history = chatService.getChatHistory(userId, page, size);
        return ResponseEntity.ok(history);
    }

    // API 2: Nhắn tin cho AI
    // Link gọi: POST /api/chat/send
    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatRequestDTO requestDTO) {
        String userId = getCurrentUserId();

        // Gọi Service xử lý, lưu DB và nhả ra câu trả lời của AI
        ChatMessageDTO aiResponse = chatService.processUserMessage(userId, requestDTO.getMessage());

        return ResponseEntity.ok(aiResponse);
    }
}
