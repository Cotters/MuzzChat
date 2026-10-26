package com.cotters.muzzchat.data

import com.cotters.muzzchat.database.Chat
import com.cotters.muzzchat.database.ChatDao
import com.cotters.muzzchat.database.ChatDatabase
import com.cotters.muzzchat.database.Message
import com.cotters.muzzchat.database.MessageDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatMessage(
    val id: Int,
    val content: String,
    val senderId: String,
    val timestamp: Long,
)

class ChatRepository(
    private val database: ChatDatabase,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) {

    private val chatDao: ChatDao = database.chatDao()
    private val messageDao: MessageDao = database.messageDao()

    init {
        createChatIfNoneExist()
    }

    private fun createChatIfNoneExist() {
        CoroutineScope(dispatcher).launch {
            runCatching { database.chatDao().getChat() }
                .onFailure { database.chatDao().createNewChat(Chat(id = 0)) }
        }
    }

    suspend fun getChatMessages(): List<ChatMessage> = withContext(dispatcher) {
        val chatId = chatDao.getChat().id
        return@withContext messageDao.getMessages(chatId).map(::toDomainModel)
    }

    private fun toDomainModel(dbMessage: Message) = ChatMessage(
        id = dbMessage.id,
        content = dbMessage.content,
        senderId = dbMessage.senderId,
        timestamp = dbMessage.timestamp,
    )

}