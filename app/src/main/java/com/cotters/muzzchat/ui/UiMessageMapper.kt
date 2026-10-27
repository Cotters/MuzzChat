package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object UiMessageMapper {
    private val GROUP_GAP_MILLIS = 20.minutes.inWholeMilliseconds
    private val SEPARATOR_GAP_MILLIS = 1.hours.inWholeMilliseconds

    fun buildChatListItems(messages: List<ChatMessage>, currentUserId: String): List<ChatListItem> = buildList {
        val currentGroup = mutableListOf<ChatViewMessage>()
        for (message in messages.sortedBy { it.timestamp }) {
            val previous = currentGroup.lastOrNull()
            if (previous == null) {
                // TODO: Add time separator
            } else if (message.timestamp - previous.timestamp > GROUP_GAP_MILLIS || previous.senderId != message.senderId) {
                add(ChatListItem.MessageBlock(currentGroup.toList()))
                currentGroup.clear()
            }
            currentGroup.add(message.toUiModel(currentUserId = currentUserId))
        }
        if (currentGroup.isNotEmpty()) {
            add(ChatListItem.MessageBlock(currentGroup.toList()))
        }
    }

    private fun ChatMessage.toUiModel(currentUserId: String): ChatViewMessage {
        return ChatViewMessage(
            id = id,
            content = content,
            senderId = senderId,
            isOutgoing = senderId == currentUserId,
            timestamp = timestamp,
        )
    }
}
