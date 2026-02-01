package com.tourly.app.chat.presentation.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.tourly.app.chat.presentation.viewmodel.GroupChatViewModel

@Composable
fun GroupChatScreen(
    tourId: Long,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GroupChatViewModel = hiltViewModel()
) {
    LaunchedEffect(tourId) {
        viewModel.setTourId(tourId)
    }

    GroupChatContent(
        viewModel = viewModel,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
