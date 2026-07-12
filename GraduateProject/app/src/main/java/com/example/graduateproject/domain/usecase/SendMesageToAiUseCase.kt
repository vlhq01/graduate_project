package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.ChatMessage
import com.example.graduateproject.domain.repository.AiChatRepository
import javax.inject.Inject

class SendMessageToAiUseCase @Inject constructor(
    private val repository: AiChatRepository
) {
    suspend operator fun invoke(query: String): Result<ChatMessage> {
        if (query.isBlank()) return Result.failure(Exception("Message cannot be empty"))
        return repository.sendMessageToAi(query)
    }
}