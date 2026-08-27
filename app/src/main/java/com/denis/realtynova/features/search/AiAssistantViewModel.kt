package com.denis.realtynova.features.search

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.SearchFilter
import com.denis.realtynova.core.domain.repository.PropertyRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.denis.realtynova.core.ai.GeminiManager

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val properties: List<Property> = emptyList(),
    val isTyping: Boolean = false
)

@HiltViewModel
class AiAssistantViewModel @Inject constructor(
    private val propertyRepository: PropertyRepository,
    private val geminiManager: GeminiManager
) : ViewModel() {

    private val _messages = mutableStateListOf<ChatMessage>()
    val messages: List<ChatMessage> = _messages

    init {
        _messages.add(
            ChatMessage(
                text = "Hello! I'm your RealtyNova AI assistant. Looking for your dream home? Try asking me about luxury villas, apartments in specific areas, or price ranges.",
                isUser = false
            )
        )
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        _messages.add(ChatMessage(text = text, isUser = true))
        
        viewModelScope.launch {
            // Add typing indicator
            val typingMessage = ChatMessage(text = "Thinking...", isUser = false, isTyping = true)
            _messages.add(typingMessage)
            
            val query = text.lowercase()
            val results = propertyRepository.searchProperties(SearchFilter(query = query))
            
            // Use Gemini for response
            val aiResponse = geminiManager.generateResponse(text, results)
            
            _messages.remove(typingMessage)
            
            _messages.add(
                ChatMessage(
                    text = aiResponse,
                    isUser = false,
                    properties = results.take(3) // Limit to top 3 for display
                )
            )
        }
    }

    fun scheduleViewing(propertyId: String) {
        viewModelScope.launch {
            val success = propertyRepository.scheduleViewing(propertyId, System.currentTimeMillis() + 86400000) // Tomorrow
            if (success) {
                _messages.add(
                    ChatMessage(
                        text = "Great! I've requested a viewing for you. A representative will contact you shortly to confirm the time.",
                        isUser = false
                    )
                )
            }
        }
    }
}
