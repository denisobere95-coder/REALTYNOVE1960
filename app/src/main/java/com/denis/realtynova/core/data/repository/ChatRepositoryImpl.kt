package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.Message
import com.denis.realtynova.core.domain.model.RecentChat
import com.denis.realtynova.core.domain.repository.ChatRepository
import com.google.firebase.database.*
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

    override fun getMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val messagesRef = database.reference.child("chats").child(chatId).child("messages")
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val messages = snapshot.children.mapNotNull { it.getValue(Message::class.java) }
                trySend(messages)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        messagesRef.addValueEventListener(listener)
        awaitClose { messagesRef.removeEventListener(listener) }
    }

    override suspend fun sendMessage(chatId: String, message: Message): Result<Unit> {
        return try {
            val messagesRef = database.reference.child("chats").child(chatId).child("messages")
            val newMessageRef = messagesRef.push()
            val messageWithId = message.copy(id = newMessageRef.key ?: "")
            newMessageRef.setValue(messageWithId).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getRecentChats(userId: String): Flow<List<RecentChat>> = callbackFlow {
        val recentRef = database.reference.child("recent_chats").child(userId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chats = snapshot.children.mapNotNull { it.getValue(RecentChat::class.java) }
                trySend(chats)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        recentRef.addValueEventListener(listener)
        awaitClose { recentRef.removeEventListener(listener) }
    }

    override fun createChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) "${userId1}_${userId2}" else "${userId2}_${userId1}"
    }

    override fun getTypingStatus(chatId: String, otherUserId: String): Flow<Boolean> = callbackFlow {
        val typingRef = database.reference.child("chats").child(chatId).child("typing").child(otherUserId)
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }

            override fun onCancelled(error: DatabaseError) {}
        }
        typingRef.addValueEventListener(listener)
        awaitClose { typingRef.removeEventListener(listener) }
    }

    override suspend fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        database.reference.child("chats").child(chatId).child("typing").child(userId).setValue(isTyping).await()
    }
}
