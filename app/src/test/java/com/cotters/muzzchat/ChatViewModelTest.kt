package com.cotters.muzzchat

import androidx.lifecycle.ViewModelStore
import com.cotters.muzzchat.data.UserRepository
import com.cotters.muzzchat.domain.ChatMessage
import com.cotters.muzzchat.domain.IChatRepository
import com.cotters.muzzchat.domain.SendMessageResult
import com.cotters.muzzchat.domain.User
import com.cotters.muzzchat.testing.MainDispatcherRule
import com.cotters.muzzchat.ui.ChatViewMessage
import com.cotters.muzzchat.ui.ChatViewModel
import com.cotters.muzzchat.ui.ChatViewState
import com.cotters.muzzchat.ui.MessageComposerState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ChatViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(UnconfinedTestDispatcher())

    private val chatRepository = FakeChatRepository()
    private val viewModelStore = ViewModelStore()
    private lateinit var viewModel: ChatViewModel
    private val composer get() = viewModel.composerState.value
    private val failure = SendMessageResult.Fail("Insert failed")

    @Before
    fun setUp() {
        viewModel = ChatViewModel(UserRepository(), chatRepository)
        viewModelStore.put("chat", viewModel)
    }

    @After
    fun tearDown() {
        viewModelStore.clear()
    }

    @Test
    fun `ensure chat exists on start`() = runTest {
        assertEquals(listOf(1L), chatRepository.ensuredChatIds)
    }

    @Test
    fun `only nonblank drafts can be sent`() = runTest {
        for (draft in listOf("", "   ", "\n\t")) {
            viewModel.onMessageChanged(draft)
            viewModel.sendMessage()
            assertFalse(composer.canSend)
            assertEquals(MessageComposerState(draft = draft), composer)
        }
        assertTrue(chatRepository.sentMessages.isEmpty())

        viewModel.onMessageChanged("Hello")
        assertTrue(composer.canSend)
    }

    @Test
    fun `send trimmed message and reset composer when message sent successfully`() = runTest {
        chatRepository.sendResult = failure
        viewModel.onMessageChanged("Failed message")
        viewModel.sendMessage()
        assertEquals(MessageComposerState(draft = "Failed message", sendFailed = true), composer)

        chatRepository.sendResult = SendMessageResult.Success
        val gate = CompletableDeferred<Unit>()
        chatRepository.sendGate = gate
        viewModel.onMessageChanged("Successful message\n ")
        assertEquals(MessageComposerState(draft = "Successful message\n ", sendFailed = false), composer)
        viewModel.sendMessage()
        assertEquals(MessageComposerState(draft = "Successful message\n ", isSending = true), composer)

        gate.complete(Unit)
        assertEquals(listOf(
            SentMessage("Failed message", "John", 1L),
            SentMessage("Successful message", "John", 1L)
        ), chatRepository.sentMessages)
        assertEquals(MessageComposerState(), composer)
    }

    @Test
    fun `preserve draft with error when message failed to send`() = runTest {
        chatRepository.sendResult = failure
        viewModel.onMessageChanged(" Hello \n")
        viewModel.sendMessage()

        assertEquals(listOf(SentMessage("Hello", "John", 1L)), chatRepository.sentMessages)
        assertEquals(MessageComposerState(draft = " Hello \n", sendFailed = true), composer)
        assertTrue(composer.canSend)
    }

    @Test
    fun `reject edits and duplicate sends when message is sending`() = runTest {
        val gate = CompletableDeferred<Unit>()
        chatRepository.sendGate = gate
        viewModel.onMessageChanged("Hello")
        viewModel.sendMessage()
        viewModel.onMessageChanged("Replacement")
        viewModel.sendMessage()

        assertEquals(MessageComposerState(draft = "Hello", isSending = true), composer)
        assertFalse(composer.canSend)
        assertEquals(1, chatRepository.sentMessages.size)

        gate.complete(Unit)
        assertEquals(MessageComposerState(), composer)
    }

    @Test
    fun `clear error when editing message`() = runTest {
        chatRepository.sendResult = failure
        viewModel.onMessageChanged("Hello")
        viewModel.sendMessage()
        assertEquals(MessageComposerState(draft = "Hello", sendFailed = true), composer)

        viewModel.onMessageChanged("Try this instead")
        assertEquals(MessageComposerState(draft = "Try this instead", sendFailed = false), composer)
    }

    @Test
    fun `preserve draft without failure when send is cancelled`() = runTest {
        val gate = CompletableDeferred<Unit>()
        chatRepository.sendGate = gate
        viewModel.onMessageChanged("Hello")
        viewModel.sendMessage()
        gate.cancel(CancellationException("Send cancelled"))

        assertEquals(MessageComposerState(draft = "Hello"), composer)
    }

    @Test
    fun `switching user during send preserves captured sender`() = runTest {
        val gate = CompletableDeferred<Unit>()
        chatRepository.sendGate = gate
        viewModel.onMessageChanged("Hello")
        viewModel.sendMessage()
        viewModel.switchUser()
        gate.complete(Unit)

        assertEquals(listOf(SentMessage("Hello", "John", 1L)), chatRepository.sentMessages)
    }

    @Test
    fun `update conversation state when new messages sent and user switches`() = runTest {
        observeState()
        chatRepository.messages.value = listOf(ChatMessage(1L, "Hello", "John", 100L))
        runCurrent()
        assertEquals(listOf(ChatViewMessage(1L, "Hello", "John", true, 100L)), viewModel.viewState.value.messages)

        viewModel.switchUser()
        runCurrent()
        assertEquals(User.Sarah, viewModel.viewState.value.currentUser)
        assertFalse(viewModel.viewState.value.messages.single().isOutgoing)

        chatRepository.messages.value += ChatMessage(2L, "Reply", "Sarah", 200L)
        runCurrent()
        assertEquals(ChatViewMessage(2L, "Reply", "Sarah", true, 200L), viewModel.viewState.value.messages.last())
    }

    private fun TestScope.observeState(): List<ChatViewState> {
        val states = mutableListOf<ChatViewState>()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.viewState.collect { states.add(it) }
        }
        runCurrent()
        return states
    }
}

private data class SentMessage(val content: String, val senderId: String, val chatId: Long)

private class FakeChatRepository : IChatRepository {
    val ensuredChatIds = mutableListOf<Long>()
    val sentMessages = mutableListOf<SentMessage>()
    val messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    var sendResult: SendMessageResult = SendMessageResult.Success
    var sendGate: CompletableDeferred<Unit>? = null

    override suspend fun ensureChatExists(chatId: Long) {
        ensuredChatIds.add(chatId)
    }

    override fun observeChatMessages(chatId: Long): Flow<List<ChatMessage>> = messages.asStateFlow()

    override suspend fun clearChat(chatId: Long) {
        error("Not used by these tests")
    }

    override suspend fun sendMessage(content: String, senderId: String, chatId: Long): SendMessageResult {
        sentMessages.add(SentMessage(content, senderId, chatId))
        sendGate?.await()
        return sendResult
    }
}
