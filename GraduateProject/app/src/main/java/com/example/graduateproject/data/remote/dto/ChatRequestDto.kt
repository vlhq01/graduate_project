package com.example.graduateproject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ChatRequestDTO(
    val message: String
)