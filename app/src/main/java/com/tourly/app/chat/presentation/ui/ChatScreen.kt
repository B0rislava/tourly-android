package com.tourly.app.chat.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.chat.presentation.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    onChatClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ChatViewModel = hiltViewModel()
) {
    LaunchedEffect(Unit) {
        viewModel.loadChats()
    }
    
    ChatContent(
        viewModel = viewModel,
        onChatClick = onChatClick,
        modifier = modifier
    )
}
