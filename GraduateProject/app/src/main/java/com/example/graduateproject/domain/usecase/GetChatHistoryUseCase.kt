package com.example.graduateproject.domain.usecase

import com.example.graduateproject.domain.model.ChatMessage
import com.example.graduateproject.domain.repository.AiChatRepository
import javax.inject.Inject

class GetChatHistoryUseCase @Inject constructor(
    private val repository: AiChatRepository
) {
    suspend operator fun invoke(): Result<List<ChatMessage>> = repository.getChatHistory()
}