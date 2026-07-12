package com.example.graduateproject.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val recommendedProducts: List<Product> = emptyList()
)

enum class ChatSender {
    USER, AI
}