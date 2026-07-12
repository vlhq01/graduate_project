package com.example.graduateproject.data.remote.dto

import com.example.graduateproject.domain.model.Product
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageDTO(
    val id: Long,
    val senderType: String, // "USER" hoặc "AI"
    val content: String,
    val createdAt: String?,
    val suggestedProducts: List<Product>? = null
)