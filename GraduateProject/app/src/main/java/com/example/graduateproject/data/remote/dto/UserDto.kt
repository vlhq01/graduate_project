package com.example.graduateproject.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String?,
    val phoneNumber: String?
)

@Serializable
data class UserSyncDTO(
    val email: String,
    val name: String,
    val avatarUrl: String? = null,
    val phoneNumber: String? = null
)