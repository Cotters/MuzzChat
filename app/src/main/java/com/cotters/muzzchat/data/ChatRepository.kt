package com.cotters.muzzchat.data

import androidx.room3.withWriteTransaction
import com.cotters.muzzchat.database.Chat
import com.cotters.muzzchat.database.ChatDao
import com.cotters.muzzchat.database.ChatDatabase
import com.cotters.muzzchat.database.Message
import com.cotters.muzzchat.database.MessageDao
import com.cotters.muzzchat.domain.ChatMessage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant

class ChatRepository(
    private val database: ChatDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val chatDao: ChatDao = database.chatDao()
    private val messageDao: MessageDao = database.messageDao()

    private suspend fun getOrCreateChatId(): Long {
        return database.withWriteTransaction {
            chatDao.getChat()?.id ?: chatDao.createNewChat(Chat())
        }
    }

    suspend fun observeChatMessages(): Flow<List<ChatMessage>> = withContext(dispatcher) {
        val chatId = getOrCreateChatId()
        return@withContext messageDao.getMessages(chatId)
            .map { dbMessages -> dbMessages.map(::toDomainModel) }
    }

    suspend fun sendMessage(content: String, senderId: String) = withContext(dispatcher) {
        messageDao.insertMessage(createNewMessage(content, senderId))
    }

    private suspend fun createNewMessage(content: String, senderId: String): Message = Message(
        chatId = getOrCreateChatId(),
        content = content,
        senderId = senderId,
        timestamp = Instant.now().toEpochMilli()
    )

    private fun toDomainModel(dbMessage: Message) = ChatMessage(
        id = dbMessage.id,
        content = dbMessage.content,
        senderId = dbMessage.senderId,
        timestamp = dbMessage.timestamp,
    )

}