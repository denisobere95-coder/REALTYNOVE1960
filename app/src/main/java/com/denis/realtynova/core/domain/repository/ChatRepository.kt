package com.denis.realtynova.core.domain.repository

import com.denis.realtynova.core.domain.model.Message
import com.denis.realtynova.core.domain.model.RecentChat
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(chatId: String): Flow<List<Message>>
    suspend fun sendMessage(chatId: String, message: Message): Result<Unit>
    fun getRecentChats(userId: String): Flow<List<RecentChat>>
    fun createChatId(userId1: String, userId2: String): String
    fun getTypingStatus(chatId: String, otherUserId: String): Flow<Boolean>
    suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean)
}
