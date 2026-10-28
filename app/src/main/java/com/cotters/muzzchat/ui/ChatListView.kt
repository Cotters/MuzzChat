package com.cotters.muzzchat.ui

import android.text.format.DateUtils
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cotters.muzzchat.ui.theme.MuzzChatTheme
import com.cotters.muzzchat.ui.viewmodel.ChatListItem
import java.time.Duration
import java.time.Instant

@Composable
fun ChatListView(
    listItems: List<ChatListItem>,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val latestMessage = (listItems.lastOrNull() as? ChatListItem.MessageBlock)
        ?.messages
        ?.lastOrNull()
    LaunchedEffect(latestMessage) {
        listState.requestScrollToItem(listItems.size)
    }
    LazyColumn(
        modifier = modifier.background(MaterialTheme.colorScheme.surface),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(
            items = listItems,
            key = { item ->
                when (item) {
                    is ChatListItem.TimeSeparator -> "time_${item.timestamp}"
                    is ChatListItem.MessageBlock -> "message_block_${item.messages.first().id}"
                }
            },
            contentType = { item ->
                when (item) {
                    is ChatListItem.TimeSeparator -> "time_separator"
                    is ChatListItem.MessageBlock -> "message_block"
                }
            }
        ) { item ->
            when (item) {
                is ChatListItem.TimeSeparator -> {
                    TimeLabelSeparator(item)
                }

                is ChatListItem.MessageBlock -> {
                    MessageBlock(item)
                }
            }
        }
        item(
            key = "CHAT_END_KEY",
            contentType = "CHAT_END_KEY",
        ) {
            Spacer(Modifier.height(1.dp))
        }
    }
}

@Composable
private fun TimeLabelSeparator(item: ChatListItem.TimeSeparator) {
    val context = LocalContext.current
    val age = Instant.now().toEpochMilli() - item.timestamp
    val flags = if (age in 0L until DateUtils.WEEK_IN_MILLIS) {
        DateUtils.FORMAT_SHOW_WEEKDAY or DateUtils.FORMAT_SHOW_TIME
    } else {
        DateUtils.FORMAT_SHOW_DATE
    }
    Text(
        text = DateUtils.formatDateTime(context, item.timestamp, flags),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        textAlign = TextAlign.Center,
    )
}

@Composable
private fun MessageBlock(item: ChatListItem.MessageBlock) {
    Column(
        verticalArrangement = Arrangement.spacedBy(1.dp)
    ) {
        item.messages.forEach { message ->
            ChatListMessageItemView(message)
        }
    }
}

@Composable
private fun ChatListMessageItemView(message: ChatViewMessage) {
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
        )
    }
}

@Composable
private fun MessageBubble(
    content: String,
    backgroundColor: Color,
    textColor: Color,
) {
    Box(
        modifier = Modifier
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
private fun ChatListViewPreview() {
    val twoWeeksAgo = Instant.now().minus(Duration.ofDays(14)).toEpochMilli()
    val twoDaysAgo = Instant.now().minus(Duration.ofDays(2)).toEpochMilli()
    MuzzChatTheme {
        ChatListView(
            listItems = listOf(
                ChatListItem.TimeSeparator(timestamp = twoWeeksAgo),
                ChatListItem.MessageBlock(
                    messages = listOf(
                        ChatViewMessage(0, "Long ago", "John", true, 1),
                    )
                ),
                ChatListItem.TimeSeparator(timestamp = twoDaysAgo),
                ChatListItem.MessageBlock(
                    messages = listOf(
                        ChatViewMessage(1, "More recent", "John", true, 1),
                        ChatViewMessage(2, "A reply", "Sarah", false, 1),
                    )
                ),
            ),
        )
    }
}