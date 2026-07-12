package com.example.graduateproject.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val avatarUrl: String? = null
)

