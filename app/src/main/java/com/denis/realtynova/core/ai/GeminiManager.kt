package com.denis.realtynova.core.ai

import com.denis.realtynova.BuildConfig
import com.denis.realtynova.core.domain.model.Property
import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.type.content
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiManager @Inject constructor() {
    private val model = FirebaseVertexAI.instance.generativeModel("gemini-1.5-flash")
    
    private val chat = model.startChat()

    suspend fun generateResponse(userPrompt: String, propertiesContext: List<Property>): String {
        val systemPrompt = """
            You are RealtyNova AI, a luxury property concierge in Kenya. 
            You help users find their dream homes.
            
            Available Properties for this user's context:
            ${propertiesContext.joinToString("\n") { "${it.title} in ${it.location} for ${it.currency} ${it.price}" }}
            
            Instructions:
            1. Be professional, sophisticated, and helpful.
            2. If you find matching properties in the context, mention them specifically.
            3. If you don't find exact matches, suggest the closest alternatives.
            4. Keep responses concise and focused on property discovery.
        """.trimIndent()

        val response = chat.sendMessage(
            content {
                text(systemPrompt)
                text(userPrompt)
            }
        )
        return response.text ?: "I'm sorry, I couldn't process that request. How else can I help you find a property today?"
    }
}
