package com.cotters.muzzchat.data

import com.cotters.muzzchat.database.Chat
import com.cotters.muzzchat.database.ChatDao
import com.cotters.muzzchat.database.ChatDatabase
import com.cotters.muzzchat.database.Message
import com.cotters.muzzchat.database.MessageDao
import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.domain.IChatRepository
import com.cotters.muzzchat.domain.SendMessageResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.Instant

class ChatRepository(database: ChatDatabase) : IChatRepository {

    private companion object {
        const val FAILED_TO_SEND_MESSAGE = "Failed to send message. Try again."
    }

    private val chatDao: ChatDao = database.chatDao()
    private val messageDao: MessageDao = database.messageDao()

    override suspend fun ensureChatExists(chatId: Long) {
        chatDao.insertIfAbsent(Chat(id = chatId))
    }

    override suspend fun clearChat(chatId: Long) {
        messageDao.deleteMessages(chatId = chatId)
    }

    override fun observeChatMessages(chatId: Long): Flow<List<ChatMessage>> {
        return messageDao.getMessages(chatId)
            .map { dbMessages -> dbMessages.map(::toDomainModel) }
    }

    override suspend fun sendMessage(content: String, senderId: String, chatId: Long): SendMessageResult {
        try {
            ensureChatExists(chatId)
            messageDao.insertMessage(createNewMessage(content = content, senderId = senderId, chatId = chatId))
            return SendMessageResult.Success
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (_: Exception) {
            return SendMessageResult.Fail(FAILED_TO_SEND_MESSAGE)
        }
    }

    private fun createNewMessage(
        content: String,
        senderId: String,
        chatId: Long,
    ): Message = Message(
        chatId = chatId,
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
