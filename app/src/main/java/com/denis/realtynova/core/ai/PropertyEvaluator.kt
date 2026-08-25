package com.denis.realtynova.core.ai

import com.denis.realtynova.core.domain.model.Property
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyEvaluator @Inject constructor(
    private val geminiManager: GeminiManager
) {
    /**
     * Evaluates a property based on its features and current market context.
     * Simulated Genkit-like 'flow' for property analysis.
     */
    suspend fun evaluateProperty(property: Property): PropertyEvaluation {
        val analysis = geminiManager.generateResponse(
            "Analyze this property for luxury value and investment potential: " +
            "${property.title} in ${property.location}. Features: ${property.bedrooms}BR, ${property.bathrooms}BA, ${property.areaSqFt}sqft. " +
            "Amenities: ${property.amenities.joinToString()}. " +
            "Provide a summary of its unique selling points and an estimated rental yield range.",
            listOf(property)
        )
        
        return PropertyEvaluation(
            propertyId = property.id,
            analysis = analysis,
            luxuryScore = calculateLuxuryScore(property)
        )
    }

    private fun calculateLuxuryScore(property: Property): Int {
        var score = 50 // Base score
        if (property.isPremium) score += 20
        if (property.isVerified) score += 10
        if (property.amenities.size > 5) score += 10
        if (property.price > 50000000) score += 10
        return score.coerceAtMost(100)
    }
}

data class PropertyEvaluation(
    val propertyId: String,
    val analysis: String,
    val luxuryScore: Int
)
