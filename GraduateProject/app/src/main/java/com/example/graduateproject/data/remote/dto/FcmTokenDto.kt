package com.example.graduateproject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenDto(
    val token: String
)