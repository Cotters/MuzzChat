package com.cotters.muzzchat.domain

import kotlinx.coroutines.flow.Flow

interface IChatRepository {
    suspend fun ensureChatExists(chatId: Long)
    suspend fun clearChat(chatId: Long)
    fun observeChatMessages(chatId: Long): Flow<List<ChatMessage>>
    suspend fun sendMessage(content: String, senderId: String, chatId: Long): SendMessageResult
}
