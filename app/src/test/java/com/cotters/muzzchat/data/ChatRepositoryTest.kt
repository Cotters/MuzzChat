package com.cotters.muzzchat.data

import com.cotters.muzzchat.database.Chat
import com.cotters.muzzchat.database.ChatDao
import com.cotters.muzzchat.database.ChatDatabase
import com.cotters.muzzchat.database.MessageDao
import com.cotters.muzzchat.domain.SendMessageResult
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test

class ChatRepositoryTest {

    @RelaxedMockK
    private lateinit var database: ChatDatabase

    @RelaxedMockK
    private lateinit var chatDao: ChatDao

    @RelaxedMockK
    private lateinit var messageDao: MessageDao

    private lateinit var repository: ChatRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { database.chatDao() } returns chatDao
        every { database.messageDao() } returns messageDao
        coEvery { chatDao.insertIfAbsent(any()) } returns -1L
        coEvery { messageDao.insertMessage(any()) } returns 7L
        repository = ChatRepository(database)
    }

    @Test
    fun `ensure chat exists first when sending messages`() = runTest {
        val result = repository.sendMessage("Hello", "Sarah", 3L)

        assertEquals(SendMessageResult.Success, result)
        coVerifySequence {
            chatDao.insertIfAbsent(Chat(id = 3L))
            messageDao.insertMessage(match {
                it.id == 0L && it.content == "Hello" && it.senderId == "Sarah" && it.chatId == 3L
            })
        }
    }

    @Test
    fun `return fail state with message when send message fails`() = runTest {
        val failure = IllegalStateException("Insert failed")
        coEvery { messageDao.insertMessage(any()) } throws failure

        val result = repository.sendMessage("Hello", "John", 1L)

        assertEquals(SendMessageResult.Fail(FAILED_TO_SEND_MESSAGE), result)
        coVerify(exactly = 1) { messageDao.insertMessage(any()) }
    }

    @Test
    fun `cancellation propagates instead of returning fail`() = runTest {
        val cancellation = CancellationException("Insert cancelled")
        coEvery { messageDao.insertMessage(any()) } throws cancellation

        try {
            repository.sendMessage("Hello", "John", 1L)
            fail("Expected cancellation to propagate")
        } catch (actual: CancellationException) {
            assertEquals(cancellation.message, actual.message)
        }
    }

    companion object {
        const val FAILED_TO_SEND_MESSAGE = "Failed to send message. Try again."
    }
}
