package com.example.techadvisor.service;

import com.example.techadvisor.client.AiServiceClient;
import com.example.techadvisor.dto.*;
import com.example.techadvisor.entity.ChatMessage;
import com.example.techadvisor.entity.Product;
import com.example.techadvisor.mapper.ChatMessageMapper;
import com.example.techadvisor.mapper.ProductMapper;
import com.example.techadvisor.repository.ChatMessageRepository;
import com.example.techadvisor.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired private ChatMessageRepository chatRepo;
    @Autowired private AiServiceClient aiServiceClient;
    @Autowired private ProductRepository productRepository;
    @Autowired private ProductMapper productMapper;
    @Autowired private ChatMessageMapper chatMessageMapper;

    public List<ChatMessageDTO> getChatHistory(String userId, int page, int size) {
        return chatRepo.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(page, size))
                .stream()
                .map(this::toDtoWithProducts)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChatMessageDTO processUserMessage(String userId, String userMessage) {
        List<ChatHistoryItemDTO> history = getRecentHistoryForAi(userId);
        Map<String, Object> context = buildContext(userId);

        ChatMessage userMsg = new ChatMessage();
        userMsg.setUserId(userId);
        userMsg.setSenderType("USER");
        userMsg.setContent(userMessage);
        userMsg.setCreatedAt(OffsetDateTime.now());
        chatRepo.save(userMsg);

        AiChatRequestDTO request = new AiChatRequestDTO(userMessage, history, context);
        AiChatResponseDTO aiResponse = aiServiceClient.chatWithAi(request);

        List<String> productIds = aiResponse.getSuggestedProductIds() != null
                ? aiResponse.getSuggestedProductIds()
                : new ArrayList<>();

        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setUserId(userId);
        aiMsg.setSenderType("AI");
        aiMsg.setContent(aiResponse.getBotReply());
        aiMsg.setCreatedAt(OffsetDateTime.now());
        aiMsg.setSuggestedProducts(productIds);
        chatRepo.save(aiMsg);

        return toDtoWithProducts(aiMsg);
    }

    private List<ChatHistoryItemDTO> getRecentHistoryForAi(String userId) {
        return chatRepo.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .sorted(Comparator.comparing(ChatMessage::getCreatedAt))
                .map(chatMessageMapper::toHistoryItem)
                .collect(Collectors.toList());
    }

    private Map<String, Object> buildContext(String userId) {
        Map<String, Object> context = new HashMap<>();

        chatRepo.findFirstByUserIdAndSenderTypeOrderByCreatedAtDesc(userId, "AI")
                .ifPresent(lastAiMessage -> context.put(
                        "lastSuggestedProductIds",
                        lastAiMessage.getSuggestedProducts()
                ));

        return context;
    }

    private ChatMessageDTO toDtoWithProducts(ChatMessage message) {
        ChatMessageDTO dto = chatMessageMapper.toDto(message);

        List<String> productIds = message.getSuggestedProducts();
        if (productIds == null || productIds.isEmpty()) {
            dto.setSuggestedProducts(Collections.emptyList());
            return dto;
        }

        Map<String, Product> productMap = productRepository.findAllById(productIds)
                .stream()
                .collect(Collectors.toMap(Product::getId, p -> p));

        List<ProductDTO> products = productIds.stream()
                .map(productMap::get)
                .filter(Objects::nonNull)
                .map(productMapper::toDto)
                .collect(Collectors.toList());

        dto.setSuggestedProducts(products);
        return dto;
    }
}