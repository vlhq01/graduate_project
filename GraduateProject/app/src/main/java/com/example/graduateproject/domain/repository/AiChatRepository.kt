package com.example.graduateproject.domain.repository

import com.example.graduateproject.domain.model.ChatMessage

interface AiChatRepository {
    suspend fun getChatHistory(): Result<List<ChatMessage>>
    suspend fun sendMessageToAi(message: String): Result<ChatMessage>
}