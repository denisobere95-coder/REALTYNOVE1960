package com.denis.realtynova.features.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.RecentChat
import com.denis.realtynova.core.domain.repository.AuthRepository
import com.denis.realtynova.core.domain.repository.ChatRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface MessagesUiState {
    data object Loading : MessagesUiState
    data class Success(val chats: List<RecentChat>) : MessagesUiState
    data class Error(val message: String) : MessagesUiState
}

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<MessagesUiState>(MessagesUiState.Loading)
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadRecentChats()
    }

    private fun loadRecentChats() {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if (user == null) {
                _uiState.value = MessagesUiState.Error("User not logged in")
                return@launch
            }
            
            chatRepository.getRecentChats(user.id)
                .onStart { _uiState.value = MessagesUiState.Loading }
                .catch { e -> _uiState.value = MessagesUiState.Error(e.message ?: "Unknown error") }
                .collect { chats ->
                    _uiState.value = MessagesUiState.Success(chats)
                }
        }
    }
}
