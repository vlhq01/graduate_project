package com.example.techadvisor.client;

import com.example.techadvisor.dto.AiChatRequestDTO;
import com.example.techadvisor.dto.AiChatResponseDTO;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.util.List;
import java.util.Map;

// url trỏ thẳng tới server Python của bạn
@HttpExchange("/api")
public interface AiServiceClient {

    @PostExchange("/chat")
    AiChatResponseDTO chatWithAi(@RequestBody AiChatRequestDTO request);

    @PostExchange("/search")
    List<String> searchHybrid(@RequestBody Map<String, Object> requestBody);

    @PostExchange("/admin/sync-embeddings")
    Map<String, Object> syncEmbeddings();

    @GetExchange("/recommend/similar/{productId}")
    List<String> getSimilarProducts(@PathVariable("productId") String productId, @RequestParam("limit") int limit);
}
