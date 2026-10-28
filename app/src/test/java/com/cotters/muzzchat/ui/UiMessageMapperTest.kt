package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.ui.viewmodel.ChatListItem
import com.cotters.muzzchat.ui.viewmodel.UiMessageMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class UiMessageMapperTest {

    @Test
    fun `group messages sent by sender`() {
        val expectedListItems = UiMessageMapper.buildChatListItems(messages, CURRENT_USER_ID)
        assertEquals(
            listOf(
                ChatListItem.TimeSeparator(timestamp = 1792863647000),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(0..0)
                ),
                ChatListItem.TimeSeparator(timestamp = 1792984247000),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(1..2)
                ),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(3..4)
                ),
                ChatListItem.TimeSeparator(timestamp = 1794075647000),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(5..6)
                ),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(7..7)
                ),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(8..8)
                ),
            ),
            expectedListItems
        )
    }

    private companion object {
        private val messages = SampleChat.messages
        private val uiMessages = messages.map(::toUiModel)
        private const val CURRENT_USER_ID: String = "John"

        private fun toUiModel(message: ChatMessage): ChatViewMessage {
            return ChatViewMessage(
                id = message.id,
                content = message.content,
                senderId = message.senderId,
                isOutgoing = message.senderId == CURRENT_USER_ID,
                timestamp = message.timestamp,
            )
        }

    }
}