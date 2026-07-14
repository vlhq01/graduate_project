package com.example.graduateproject.presentation.aichat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.graduateproject.domain.model.ChatMessage
import com.example.graduateproject.domain.model.ChatSender
import com.example.graduateproject.domain.usecase.GetChatHistoryUseCase
import com.example.graduateproject.domain.usecase.SendMessageToAiUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val getChatHistoryUseCase: GetChatHistoryUseCase,
    private val sendMessageToAiUseCase: SendMessageToAiUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(AiChatState())
    val state = _state.asStateFlow()

    private val _effect = MutableSharedFlow<AiChatEffect>()

    init {
        processIntent(AiChatIntent.LoadHistory)
    }

    fun processIntent(intent: AiChatIntent) {
        when (intent) {
            is AiChatIntent.LoadHistory -> loadHistory()
            is AiChatIntent.UpdateInput -> _state.update { it.copy(inputText = intent.text) }
            is AiChatIntent.SendMessage -> sendMessage()
            is AiChatIntent.ClickProduct -> navigateToProduct(intent.productId)
            is AiChatIntent.AddToWorkspace -> addToWorkspace(intent.productId)
        }
    }

    private fun loadHistory() {
        viewModelScope.launch {
            getChatHistoryUseCase().onSuccess { history ->
                _state.update { it.copy(messages = history.toPersistentList()) }
                _effect.emit(AiChatEffect.ScrollToBottom)
            }
        }
    }

    private fun sendMessage() {
        val query = _state.value.inputText.trim()
        Log.d("xxx", "sendMessage: $query")
        if (query.isEmpty() || _state.value.isAiTyping) return

        viewModelScope.launch {
            val userMessage = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.USER,
                text = query
            )

            _state.update {
                it.copy(
                    inputText = "",
                    messages = (it.messages + userMessage).toPersistentList(),
                    isAiTyping = true,
                    error = null
                )
            }
            _effect.emit(AiChatEffect.ScrollToBottom)

            sendMessageToAiUseCase(query)
                .onSuccess { aiResponse ->
                    _state.update {
                        it.copy(
                            isAiTyping = false,
                            messages = (it.messages + aiResponse).toPersistentList()
                        )
                    }
                    _effect.emit(AiChatEffect.ScrollToBottom)
                }
                .onFailure { error ->
                    _state.update { it.copy(isAiTyping = false, error = error.message) }
                    _effect.emit(AiChatEffect.ShowToast("Lỗi kết nối AI: ${error.message}"))
                }
        }
    }

    private fun navigateToProduct(productId: String) {
        viewModelScope.launch { _effect.emit(AiChatEffect.NavigateToDetail(productId)) }
    }

    private fun addToWorkspace(productId: String) {
        viewModelScope.launch {
            _effect.emit(AiChatEffect.ShowToast("Added to Workspace!"))
        }
    }
}