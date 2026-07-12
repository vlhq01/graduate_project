package com.example.graduateproject.data.mapper

import com.example.graduateproject.data.remote.dto.UserDto
import com.example.graduateproject.domain.model.User

fun UserDto.toDomainUser(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
    )
}