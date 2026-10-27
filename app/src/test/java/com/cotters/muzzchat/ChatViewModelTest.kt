package com.cotters.muzzchat

import androidx.lifecycle.ViewModelStore
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.domain.IChatRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {

    @Test
    fun `ensure chat exists on start`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val viewModelStore = ViewModelStore()

        try {
            val chatRepository = FakeChatRepository()
            val viewModel = ChatViewModel(UserRepository(), chatRepository)
            viewModelStore.put("chat", viewModel)

            runCurrent()

            assertEquals(listOf(1L), chatRepository.ensuredChatIds)
        } finally {
            viewModelStore.clear()
            Dispatchers.resetMain()
        }
    }
}

private class FakeChatRepository : IChatRepository {
    val ensuredChatIds = mutableListOf<Long>()

    override suspend fun ensureChatExists(chatId: Long) {
        ensuredChatIds.add(chatId)
    }

    override fun observeChatMessages(chatId: Long): Flow<List<ChatMessage>> =
        flowOf(emptyList())

    override suspend fun clearChat(chatId: Long) {
        error("Not used by the initialization test")
    }

    override suspend fun sendMessage(content: String, senderId: String, chatId: Long) {
        error("Not used by the initialization test")
    }
}
