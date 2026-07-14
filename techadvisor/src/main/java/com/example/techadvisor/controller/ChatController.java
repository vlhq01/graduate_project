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

    private String getCurrentUserId() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatMessageDTO>> getHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        String userId = getCurrentUserId();
        List<ChatMessageDTO> history = chatService.getChatHistory(userId, page, size);
        return ResponseEntity.ok(history);
    }


    @PostMapping("/send")
    public ResponseEntity<ChatMessageDTO> sendMessage(@RequestBody ChatRequestDTO requestDTO) {
        String userId = getCurrentUserId();

        ChatMessageDTO aiResponse = chatService.processUserMessage(userId, requestDTO.getMessage());

        return ResponseEntity.ok(aiResponse);
    }
}
