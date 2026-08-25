package com.denis.realtynova.core.util

import com.denis.realtynova.core.ai.GeminiManager
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.repository.AuthRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SmartAlertManager @Inject constructor(
    private val database: FirebaseDatabase,
    private val geminiManager: GeminiManager,
    private val authRepository: AuthRepository
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val alertsRef = database.getReference("smart_alerts")

    fun onNewPropertyAdded(property: Property) {
        scope.launch {
            val user = authRepository.currentUser.first() ?: return@launch
            
            // 1. Fetch user preferences from RTDB
            val prefs = fetchUserPreferences(user.id) ?: return@launch
            
            // 2. Simple matching logic
            if (isMatch(property, prefs)) {
                // 3. Generate personalized notification text using Gemini
                val message = geminiManager.generateResponse(
                    "A new property just listed: ${property.title} in ${property.location} for ${property.currency} ${property.price}. " +
                    "User preferences: ${prefs["interest"]}. " +
                    "Write a short, exciting notification message for this user.",
                    listOf(property)
                )
                
                // 4. Save alert to user's notifications in RTDB
                saveAlert(user.id, property.id, message)
            }
        }
    }

    private suspend fun fetchUserPreferences(userId: String): Map<String, Any>? {
        return try {
            val snapshot = database.getReference("user_prefs").child(userId).get().await()
            snapshot.value as? Map<String, Any>
        } catch (e: Exception) {
            null
        }
    }

    private fun isMatch(property: Property, prefs: Map<String, Any>): Boolean {
        val interest = prefs["interest"]?.toString()?.lowercase() ?: ""
        return property.title.lowercase().contains(interest) || 
               property.location.lowercase().contains(interest) ||
               property.type.lowercase().contains(interest)
    }

    private suspend fun saveAlert(userId: String, propertyId: String, message: String) {
        val alert = mapOf(
            "propertyId" to propertyId,
            "message" to message,
            "timestamp" to System.currentTimeMillis(),
            "isRead" to false
        )
        alertsRef.child(userId).push().setValue(alert).await()
    }
}
