package com.denis.realtynova.core.domain.model

data class TrustScore(
    val overallScore: Int,
    val isOwnerVerified: Boolean = false,
    val isLocationVerified: Boolean = false,
    val isImagesVerified: Boolean = false,
    val isAgentVerified: Boolean = false,
    val isDocumentsChecked: Boolean = false,
    val lastInspectedDate: String? = null
) {
    val level: TrustLevel
        get() = when {
            overallScore >= 90 -> TrustLevel.EXCEPTIONAL
            overallScore >= 75 -> TrustLevel.HIGH
            overallScore >= 50 -> TrustLevel.MODERATE
            else -> TrustLevel.LOW
        }
}

enum class TrustLevel {
    EXCEPTIONAL, HIGH, MODERATE, LOW
}
