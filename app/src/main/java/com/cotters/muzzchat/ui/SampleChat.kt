package com.cotters.muzzchat.ui

import com.cotters.muzzchat.domain.ChatMessage
import java.time.Duration
import java.time.Instant

object SampleChat {
    val sampleStart: Instant = Instant.now().minus(Duration.ofHours(6))

    private fun sampleTimestamp(minutesAfterStart: Long, seconds: Long = 0L): Long =
        sampleStart
            .plus(Duration.ofMinutes(minutesAfterStart))
            .plus(Duration.ofSeconds(seconds))
            .toEpochMilli()
    val messages = listOf(
        ChatMessage(id = 0, content = "Hi John", senderId = "Sarah", timestamp = sampleTimestamp(0)),
        ChatMessage(id = 1, content = "Let's do Tuesday.", senderId = "Sarah", timestamp = sampleTimestamp(10L)),
        ChatMessage(id = 2, content = "What time?", senderId = "Sarah", timestamp = sampleTimestamp(31L)),
        ChatMessage(id = 3, content = "Tuesday works. 3pm ok?", senderId = "John", timestamp = sampleTimestamp(32L)),
        ChatMessage(id = 4, content = "3pm is great see you then", senderId = "John", timestamp = sampleTimestamp(32L)),
        ChatMessage(id = 5, content = "I'm running late", senderId = "John", timestamp = sampleTimestamp(200L)),
        ChatMessage(id = 6, content = "Be 30 mins", senderId = "John", timestamp = sampleTimestamp(201L)),
        ChatMessage(id = 7, content = "I'm here now, where are you?", senderId = "John", timestamp = sampleTimestamp(231L)),
        ChatMessage(id = 8, content = "I'm sat by the palm tree", senderId = "Sarah", timestamp = sampleTimestamp(232L)),
    )
}