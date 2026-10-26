package com.cotters.muzzchat.database

import androidx.room3.ColumnInfo
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.ForeignKey
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase

@Database(entities = [Chat::class, Message::class], version = 1)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}

@Entity(tableName = "chats")
data class Chat(
    @PrimaryKey(autoGenerate = true) val id: Int,
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = Chat::class,
            parentColumns = ["id"],
            childColumns = ["chatId"],
            onDelete = ForeignKey.CASCADE,
        )
    ]
)
data class Message(
    @PrimaryKey val id: Int,
    @ColumnInfo(index = true) val chatId: Int,
    val content: String,
    val senderId: String,
    val timestamp: Long,
)

@Dao
interface ChatDao {
    @Insert
    fun createNewChat(chat: Chat): Long

    @Query("SELECT * FROM chats LIMIT 1")
    fun getChat(): Chat
}

@Dao
interface MessageDao {
    @Query("SELECT * FROM chat_messages WHERE chatId = :chatId")
    suspend fun getMessages(chatId: Int): List<Message>
}
