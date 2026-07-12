package com.example.graduateproject.presentation.aichat

import androidx.compose.runtime.Immutable
import com.example.graduateproject.domain.model.ChatMessage
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf

@Immutable
data class AiChatState(
    val messages: PersistentList<ChatMessage> = persistentListOf(),
    val inputText: String = "",
    val isAiTyping: Boolean = false,
    val error: String? = null
)

sealed interface AiChatIntent {
    object LoadHistory : AiChatIntent
    data class UpdateInput(val text: String) : AiChatIntent
    object SendMessage : AiChatIntent
    data class ClickProduct(val productId: String) : AiChatIntent
    data class AddToWorkspace(val productId: String) : AiChatIntent
}

sealed interface AiChatEffect {
    object ScrollToBottom : AiChatEffect
    data class NavigateToDetail(val productId: String) : AiChatEffect
    data class ShowToast(val message: String) : AiChatEffect
}