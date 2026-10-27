package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import java.time.Duration
import java.time.Instant

object SampleChat {
    val chatStartTime: Instant = Instant.ofEpochMilli(1792863647000)

    private fun sampleTimestamp(minutesAfterStart: Long): Long =
        chatStartTime
            .plus(Duration.ofMinutes(minutesAfterStart))
            .toEpochMilli()
    val messages = listOf(
        ChatMessage(id = 0, content = "Hi John", senderId = "Sarah", timestamp = sampleTimestamp(0L)),
        ChatMessage(id = 1, content = "Let's meet up on Tuesday", senderId = "Sarah", timestamp = sampleTimestamp(2010L)),
        ChatMessage(id = 2, content = "Let me know what time", senderId = "Sarah", timestamp = sampleTimestamp(2021L)),
        ChatMessage(id = 3, content = "Tuesday works. 3pm ok?", senderId = "John", timestamp = sampleTimestamp(2032L)),
        ChatMessage(id = 4, content = "3pm is great see you then", senderId = "John", timestamp = sampleTimestamp(2032L)),
        ChatMessage(id = 5, content = "I'm running late", senderId = "John", timestamp = sampleTimestamp(20_200L)),
        ChatMessage(id = 6, content = "Be 30 mins", senderId = "John", timestamp = sampleTimestamp(20_201L)),
        ChatMessage(id = 7, content = "I'm here now, where are you?", senderId = "John", timestamp = sampleTimestamp(20_231L)),
        ChatMessage(id = 8, content = "I'm sat by the palm tree", senderId = "Sarah", timestamp = sampleTimestamp(20_232L)),
    )
}