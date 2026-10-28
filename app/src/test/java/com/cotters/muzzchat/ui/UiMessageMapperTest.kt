package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.ui.viewmodel.ChatListItem
import com.cotters.muzzchat.ui.viewmodel.UiMessageMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class UiMessageMapperTest {

    @Test
    fun `empty messages produce no list items`() {
        assertEquals(
            emptyList<ChatListItem>(),
            UiMessageMapper.buildChatListItems(emptyList(), CURRENT_USER_ID),
        )
    }

    @Test
    fun `single message has an initial separator and message block`() {
        val message = message(id = 1L, timestamp = 100L)

        assertEquals(
            listOf(
                ChatListItem.TimeSeparator(100L),
                ChatListItem.MessageBlock(listOf(toUiModel(message))),
            ),
            UiMessageMapper.buildChatListItems(listOf(message), CURRENT_USER_ID),
        )
    }

    @Test
    fun `messages are sorted by ascending timestamp`() {
        val first = message(id = 1L, timestamp = 0L)
        val second = message(id = 2L, timestamp = 10_000L)
        val third = message(id = 3L, timestamp = 25_000L)

        assertEquals(
            listOf(
                ChatListItem.TimeSeparator(0L),
                ChatListItem.MessageBlock(listOf(first, second, third).map(::toUiModel)),
            ),
            UiMessageMapper.buildChatListItems(listOf(third, first, second), CURRENT_USER_ID),
        )
    }

    @Test
    fun `equal timestamps preserve order and different senders split blocks`() {
        val first = message(id = 1L, timestamp = 0L, senderId = "John")
        val second = message(id = 2L, timestamp = 0L, senderId = "Sarah")

        assertEquals(
            listOf(
                ChatListItem.TimeSeparator(0L),
                ChatListItem.MessageBlock(listOf(toUiModel(first))),
                ChatListItem.MessageBlock(listOf(toUiModel(second))),
            ),
            UiMessageMapper.buildChatListItems(listOf(first, second), CURRENT_USER_ID),
        )
    }

    @Test
    fun `group splits at twenty seconds`() {
        val cases = listOf(
            19_999L to listOf(2),
            20_000L to listOf(1, 1),
            20_001L to listOf(1, 1),
        )

        for ((timestampGap, expectedBlockSizes) in cases) {
            val messages = listOf(message(1L, 0L), message(2L, timestampGap))
            val result = UiMessageMapper.buildChatListItems(messages, CURRENT_USER_ID)
            val blocks = result.filterIsInstance<ChatListItem.MessageBlock>()

            assertEquals("Gap: $timestampGap ms", expectedBlockSizes, blocks.map { it.messages.size })
            assertEquals(messages.map(::toUiModel), blocks.flatMap { it.messages })
            assertEquals(
                listOf(ChatListItem.TimeSeparator(0L)),
                result.filterIsInstance<ChatListItem.TimeSeparator>(),
            )
        }
    }

    @Test
    fun `additional time separator requires more than one hour`() {
        val cases = listOf(
            3_599_999L to listOf(0L),
            3_600_000L to listOf(0L),
            3_600_001L to listOf(0L, 3_600_001L),
        )

        for ((gap, expectedTimestamps) in cases) {
            val messages = listOf(message(1L, 0L), message(2L, gap))
            val result = UiMessageMapper.buildChatListItems(messages, CURRENT_USER_ID)

            assertEquals(
                "Gap: $gap ms",
                expectedTimestamps,
                result.filterIsInstance<ChatListItem.TimeSeparator>().map { it.timestamp },
            )
            assertEquals(
                listOf(1, 1),
                result.filterIsInstance<ChatListItem.MessageBlock>().map { it.messages.size },
            )
        }
    }

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
                    messages = uiMessages.slice(5..5)
                ),
                ChatListItem.MessageBlock(
                    messages = uiMessages.slice(6..6)
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

        private fun message(id: Long, timestamp: Long, senderId: String = CURRENT_USER_ID) =
            ChatMessage(id = id, content = "Message $id", senderId = senderId, timestamp = timestamp)

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
