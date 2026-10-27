package com.cotters.muzzchat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.domain.User
import com.cotters.muzzchat.ui.theme.MuzzChatTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MuzzApp(
    viewState: ChatViewState,
    onSwitchUserTapped: () -> Unit,
    onSendMessage: (String) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // TODO: Replace with Profile Image.
                        Icon(
                            painterResource(R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                        Text(viewState.recipient.name)
                    }
                },
                actions = {
                    TextButton(onClick = onSwitchUserTapped) {
                        Text("Switch user")
                    }
                }
            )
        }
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
                messages = viewState.messages,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            )
            ComposeMessageView(onSendMessage = onSendMessage)
        }
    }
}

@Composable
fun ComposeMessageView(
    onSendMessage: (String) -> Unit,
) {
    var message by rememberSaveable { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            value = message,
            onValueChange = { newValue -> message = newValue },
            modifier = Modifier.weight(1f),
        )
        Button(
            onClick = {
                onSendMessage(message)
                message = ""
            },
            enabled = message.isNotBlank(),
        ) {
            Text("Send")
        }
    }
}

@Composable
fun ChatListView(
    messages: List<ChatViewMessage>,
    modifier: Modifier = Modifier,
) {
    LazyColumn(modifier = modifier) {
        items(messages) { message ->
            ChatListMessageItemView(message)
        }
    }
}

@Composable
fun ChatListMessageItemView(message: ChatViewMessage) {
    val arrangement = if (message.isOutgoing) Arrangement.End else Arrangement.Start
    val backgroundColor = if (message.isOutgoing) MaterialTheme.colorScheme.primaryContainer else
        MaterialTheme.colorScheme.surfaceContainerHigh
    val textColor = if (message.isOutgoing) MaterialTheme.colorScheme.onPrimaryContainer else
        MaterialTheme.colorScheme.onSurface
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = arrangement
    ) {
        MessageBubble(
            content = message.content,
            backgroundColor = backgroundColor,
            textColor = textColor,
            modifier = Modifier,
        )
    }
}

@Composable
fun MessageBubble(
    content: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .padding(8.dp)
            .widthIn(max = 300.dp)
    ) {
        Text(content, modifier = Modifier.padding(4.dp), color = textColor)
    }
}

@Preview
@Composable
private fun MuzzAppPreview() {
    val storedMessages = listOf(
        ChatViewMessage(id = 0, content = "Hi John", senderId = "Sarah", isOutgoing = false, timestamp = 0L),
        ChatViewMessage(id = 1, content = "Let's do Tuesday. What time?", isOutgoing = false, senderId = "Sarah", timestamp = 0L),
        ChatViewMessage(id = 2, content = "Tuesday works. 3pm ok?", isOutgoing = false, senderId = "John", timestamp = 0L),
    )
    MuzzChatTheme {
        MuzzApp(
            viewState = ChatViewState(
                currentUser = User.John,
                messages = storedMessages,
            ),
            onSwitchUserTapped = {},
            onSendMessage = { _ -> },
        )
    }
}