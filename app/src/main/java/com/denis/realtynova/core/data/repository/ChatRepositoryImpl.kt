package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.Message
import com.denis.realtynova.core.domain.model.RecentChat
import com.denis.realtynova.core.domain.repository.ChatRepository
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : ChatRepository {

    private val messagesRef = database.getReference("messages")
    private val chatsRef = database.getReference("chats")
    private val typingRef = database.getReference("typing")

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(MessageDto::class.java)?.toDomain(it.key ?: "") }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesRef.child(chatId).addValueEventListener(listener)
        awaitClose { messagesRef.child(chatId).removeEventListener(listener) }
    }

    override suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val dto = MessageDto.fromDomain(message)
            messagesRef.child(chatId).push().setValue(dto).await()
            
            // Update last message for both users in recent chats
            val lastMsgUpdate = mapOf(
                "lastMessage" to message.content,
                "lastMessageTime" to message.createdAt
            )
            chatsRef.child(message.senderId).child(message.receiverId).updateChildren(lastMsgUpdate)
            chatsRef.child(message.receiverId).child(message.senderId).updateChildren(lastMsgUpdate)
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRecentChats(userId: String): Flow<List<RecentChat>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { it.getValue(RecentChatDto::class.java)?.toDomain(it.key ?: "") }
                trySend(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        chatsRef.child(userId).addValueEventListener(listener)
        awaitClose { chatsRef.child(userId).removeEventListener(listener) }
    }

    override fun createChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    override fun getTypingStatus(chatId: String, otherUserId: String): Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        typingRef.child(chatId).child(otherUserId).addValueEventListener(listener)
        awaitClose { typingRef.child(chatId).child(otherUserId).removeEventListener(listener) }
    }

    override suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        typingRef.child(chatId).child(userId).setValue(isTyping).await()
    }
}

data class MessageDto(
    val senderId: String = "",
    val receiverId: String = "",
    val content: String = "",
    val createdAt: Long = 0,
    val isRead: Boolean = false
) {
    fun toDomain(id: String) = Message(id, senderId, receiverId, content, createdAt, isRead)
    
    companion object {
        fun fromDomain(message: Message) = MessageDto(
            message.senderId, message.receiverId, message.content, message.createdAt, message.isRead
        )
    }
}

data class RecentChatDto(
    val otherUserName: String = "",
    val otherUserPhoto: String? = null,
    val lastMessage: String = "",
    val lastMessageTime: Long = 0,
    val unreadCount: Int = 0
) {
    fun toDomain(otherUserId: String) = RecentChat(
        otherUserId, otherUserName, otherUserPhoto, lastMessage, lastMessageTime, unreadCount
    )
}
