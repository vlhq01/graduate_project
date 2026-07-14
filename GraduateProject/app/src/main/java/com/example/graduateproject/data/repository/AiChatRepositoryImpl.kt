package com.example.graduateproject.data.repository

import com.example.graduateproject.data.remote.AiChatApiService
import com.example.graduateproject.data.remote.dto.ChatRequestDTO
import com.example.graduateproject.domain.model.ChatMessage
import com.example.graduateproject.domain.model.ChatSender
import com.example.graduateproject.domain.repository.AiChatRepository
import javax.inject.Inject

class AiChatRepositoryImpl @Inject constructor(
    private val apiService: AiChatApiService
) : AiChatRepository {

    // 1. LẤY LỊCH SỬ TỪ SERVER
    override suspend fun getChatHistory(): Result<List<ChatMessage>> {
        return try {
            val response = apiService.getChatHistory(page = 0, size = 50) // Lấy tạm 50 tin mới nhất

            if (response.isSuccessful) {
                val dtos = response.body() ?: emptyList()

                val domainMessages = dtos.map {
                    ChatMessage(
                        id = it.id.toString(),
                        sender = if (it.senderType == "AI") ChatSender.AI else ChatSender.USER,
                        text = it.content,
                        recommendedProducts = it.suggestedProducts
                            ?: emptyList()
                    )
                }.reversed()

                Result.success(domainMessages)
            } else {
                Result.failure(Exception("Lỗi tải lịch sử: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 2. GỬI TIN NHẮN CHO AI
    override suspend fun sendMessageToAi(message: String): Result<ChatMessage> {
        return try {
            val request = ChatRequestDTO(message = message)
            val response = apiService.sendMessage(request)

            if (response.isSuccessful) {
                val aiReplyDto = response.body() ?: throw Exception("Body rỗng")

                val aiMessage = ChatMessage(
                    id = aiReplyDto.id.toString(),
                    sender = ChatSender.AI,
                    text = aiReplyDto.content,
                    recommendedProducts = aiReplyDto.suggestedProducts
                        ?: emptyList()
                )

                Result.success(aiMessage)
            } else {
                Result.failure(Exception("Lỗi gửi tin: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
