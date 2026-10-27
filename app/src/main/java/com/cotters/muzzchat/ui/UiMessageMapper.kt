package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

object UiMessageMapper {
    private val GROUP_GAP_MILLIS = 20.minutes.inWholeMilliseconds
    private val TIME_SEPARATOR_GAP_MILLIS = 1.hours.inWholeMilliseconds

    fun buildChatListItems(messages: List<ChatMessage>, currentUserId: String): List<ChatListItem> = buildList {
        val currentGroup = mutableListOf<ChatViewMessage>()
        for (message in messages.sortedBy { it.timestamp }) {
            val previousMessage = currentGroup.lastOrNull()

            if (previousMessage == null && lastOrNull() == null) {
                add(ChatListItem.TimeSeparator(message.timestamp))
            }
            if (previousMessage != null && (message.timestamp - previousMessage.timestamp > GROUP_GAP_MILLIS || previousMessage.senderId != message.senderId)) {
                add(ChatListItem.MessageBlock(currentGroup.toList()))
                currentGroup.clear()
            }
            if (previousMessage != null && message.timestamp - previousMessage.timestamp > TIME_SEPARATOR_GAP_MILLIS) {
                if (lastOrNull() !is ChatListItem.TimeSeparator) {
                    add(ChatListItem.TimeSeparator(message.timestamp))
                }
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
