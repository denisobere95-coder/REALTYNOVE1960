package com.denis.realtynova.core.domain.model

data class Message(
    val id: String,
    val senderId: String,
    val receiverId: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

data class RecentChat(
    val otherUserId: String,
    val otherUserName: String,
    val otherUserPhoto: String?,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int
)
