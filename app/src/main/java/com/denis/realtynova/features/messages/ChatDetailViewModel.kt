package com.denis.realtynova.features.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Message
import com.denis.realtynova.core.domain.model.User
import com.denis.realtynova.core.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface ChatUiState {
    data object Loading : ChatUiState
    data class Success(
        val messages: List<Message>,
        val currentUser: User?
    ) : ChatUiState
    data class Error(val message: String) : ChatUiState
}

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<ChatUiState>(ChatUiState.Loading)
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        loadMessages()
    }

    private fun loadMessages() {
        viewModelScope.launch {
            _uiState.value = ChatUiState.Success(
                messages = emptyList(),
                currentUser = authRepository.currentUser.firstOrNull()
            )
        }
    }

    fun sendMessage(content: String) {
        // Implementation
    }
}
