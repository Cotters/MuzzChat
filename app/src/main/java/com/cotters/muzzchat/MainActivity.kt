package com.cotters.muzzchat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cotters.muzzchat.ui.MuzzApp
import com.cotters.muzzchat.ui.theme.MuzzChatTheme
import com.cotters.muzzchat.ui.viewmodel.ChatViewModel
import com.cotters.muzzchat.ui.viewmodel.ChatViewState
import com.cotters.muzzchat.ui.viewmodel.MessageComposerState

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val viewModel = ViewModelProvider(this, createChatViewModelFactory(applicationContext))[ChatViewModel::class.java]
        setContent {
            val viewState: ChatViewState by viewModel.viewState.collectAsStateWithLifecycle()
            val composerState: MessageComposerState by viewModel.composerState.collectAsStateWithLifecycle()
            MuzzChatTheme {
                MuzzApp(
                    viewState = viewState,
                    composerState = composerState,
                    onSwitchUserTapped = viewModel::switchUser,
                    onClearChatTapped = viewModel::onClearChatTapped,
                    onMessageChanged = viewModel::onMessageChanged,
                    onSendMessage = viewModel::sendMessage,
                )
            }
        }
    }
}
