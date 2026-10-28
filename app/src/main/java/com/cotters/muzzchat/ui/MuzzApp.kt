package com.cotters.muzzchat.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.domain.User
import com.cotters.muzzchat.ui.theme.MuzzChatTheme
import com.cotters.muzzchat.ui.viewmodel.ChatViewEvent
import com.cotters.muzzchat.ui.viewmodel.ChatViewState
import com.cotters.muzzchat.ui.viewmodel.MessageComposerState
import com.cotters.muzzchat.ui.viewmodel.UiMessageMapper

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuzzApp(
    viewState: ChatViewState,
    composerState: MessageComposerState,
    onViewEvent: (ChatViewEvent) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                recipientName = viewState.recipient.name,
                onSwitchUserTapped = { onViewEvent(ChatViewEvent.SwitchUser) },
                onClearChatTapped = { onViewEvent(ChatViewEvent.ClearChat) }
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .padding(horizontal = 4.dp),
        ) {
            ChatListView(
                listItems = viewState.listItems,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            ComposeMessageView(
                state = composerState,
                onMessageChanged = { onViewEvent(ChatViewEvent.ComposeMessageChanged(it)) },
                onSendMessage = { onViewEvent(ChatViewEvent.SendMessageTapped) },
                modifier = Modifier.padding(top = 8.dp, bottom = 12.dp)
            )
        }
    }
}

@Preview
@Composable
private fun MuzzAppPreview() {
    val listItems = UiMessageMapper.buildChatListItems(
        messages = SampleChat.messages,
        currentUserId = "John"
    )
    MuzzChatTheme {
        MuzzApp(
            viewState = ChatViewState(
                currentUser = User.John,
                listItems = listItems,
            ),
            composerState = MessageComposerState(),
            onViewEvent = {},
        )
    }
}
