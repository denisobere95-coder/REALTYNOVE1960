package com.denis.realtynova.core.ai

import com.denis.realtynova.core.domain.model.Property
import com.google.firebase.vertexai.FirebaseVertexAI
import com.google.firebase.vertexai.type.content
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GeminiManager @Inject constructor() {
    private val model by lazy { 
        try {
            FirebaseVertexAI.instance.generativeModel("gemini-1.5-flash")
        } catch (e: Exception) {
            Timber.e(e, "Failed to initialize Gemini model")
            null
        }
    }
    
    // Use a managed chat session for context persistence
    private var chatSession: com.google.firebase.vertexai.Chat? = null

    suspend fun generateResponse(userPrompt: String, propertiesContext: List<Property>): String {
        val currentModel = model ?: return "RealtyNova AI is currently offline while we upgrade our systems. Please check back shortly!"
        
        val currentChat = chatSession ?: currentModel.startChat().also { chatSession = it }
        
        val contextInfo = if (propertiesContext.isNotEmpty()) {
            "Available Properties for this user's context:\n" + 
            propertiesContext.joinToString("\n") { 
                "• ${it.title} (${it.type}) in ${it.location} - ${it.currency} ${it.price}. Listing Type: ${it.listingType}" 
            }
        } else {
            "No specific property listings are currently in view, but you can suggest general luxury areas in Kenya like Karen, Muthaiga, or Westlands."
        }

        val systemPrompt = """
            You are RealtyNova AI, the world's most sophisticated luxury property concierge based in Nairobi, Kenya.
            Your personality: Elegant, knowledgeable, discreet, and highly efficient.
            
            $contextInfo
            
            Your Goals:
            1. Help the user discover premium real estate that matches their lifestyle.
            2. Provide insights into Kenyan neighborhoods (security, amenities, schools).
            3. Act as a bridge between the user and our premium agents.
            
            Guidelines:
            - Use professional yet inviting language.
            - If matching properties exist, prioritize them but explain WHY they fit the user's request.
            - Format prices clearly (e.g., KSh 150M).
            - If you don't know something, offer to connect them with a human specialist.
        """.trimIndent()

        return try {
            val response = currentChat.sendMessage(
                content {
                    text(systemPrompt)
                    text(userPrompt)
                }
            )
            response.text ?: "I'm refining my search for you. Could you provide more details about your preferred location?"
        } catch (e: Exception) {
            Timber.e(e, "Gemini generation failed")
            // Reset chat session on critical error to prevent stale state
            chatSession = currentModel.startChat()
            "I'm currently experiencing a high volume of requests. Please tell me a bit more about the type of property you are looking for, and I'll get back to you shortly."
        }
    }
}
