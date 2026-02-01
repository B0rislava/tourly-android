package com.tourly.app.chat.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tourly.app.chat.domain.usecase.GetMessagesUseCase
import com.tourly.app.chat.domain.usecase.SendMessageUseCase
import com.tourly.app.chat.presentation.state.GroupChatUiState
import com.tourly.app.core.network.Result
import com.tourly.app.home.domain.usecase.GetTourDetailsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val getTourDetailsUseCase: GetTourDetailsUseCase
) : ViewModel() {

    private var tourId: Long = -1L
    
    private val _uiState = MutableStateFlow(GroupChatUiState())
    val uiState = _uiState.asStateFlow()

    private var messagesJob: Job? = null

    fun setTourId(id: Long) {
        tourId = id
        fetchTourTitle()
        observeMessages()
    }

    private fun fetchTourTitle() {
        if (tourId == -1L) return
        viewModelScope.launch {
            when (val result = getTourDetailsUseCase(tourId)) {
                is Result.Success -> {
                    _uiState.update { it.copy(tourTitle = result.data.title) }
                }
                is Result.Error -> {
                    // Handle or ignore
                }
            }
        }
    }

    private fun observeMessages() {
        if (tourId == -1L) return
        
        messagesJob?.cancel()
        messagesJob = viewModelScope.launch {
            getMessagesUseCase(tourId).collect { messages ->
                println("GroupChatViewModel: Received ${messages.size} messages")
                if (messages.isNotEmpty()) {
                    val last = messages.last()
                    println("GroupChatViewModel: Last msg: id=${last.id}, content='${last.content}', senderId=${last.senderId}, isFromMe=${last.isFromMe}")
                }
                _uiState.update { it.copy(messages = messages) }
            }
        }
    }

    fun onMessageChange(content: String) {
        _uiState.update { it.copy(currentMessage = content) }
    }

    fun sendMessage() {
        val content = _uiState.value.currentMessage.trim()
        if (content.isEmpty() || tourId == -1L) return

        viewModelScope.launch {
            _uiState.update { it.copy(currentMessage = "") } // Clear input immediately for better UX
            val result = sendMessageUseCase(tourId, content)
            if (result is Result.Error) {
                _uiState.update { it.copy(error = result.message) }
            }
        }
    }
}
