package com.denis.realtynova.core.navigation

import kotlinx.serialization.Serializable

sealed interface Route {
    @Serializable
    data object Splash : Route

    @Serializable
    data object Onboarding : Route

    @Serializable
    data object AuthGraph : Route
    
    @Serializable
    data object Welcome : Route
    
    @Serializable
    data object Login : Route

    @Serializable
    data object Register : Route

    @Serializable
    data object PhoneLogin : Route

    @Serializable
    data class Otp(val phoneNumber: String) : Route

    @Serializable
    data object AccountType : Route
    
    @Serializable
    data object MainGraph : Route

    @Serializable
    data object Home : Route

    @Serializable
    data object Search : Route

    @Serializable
    data object Map : Route

    @Serializable
    data object Saved : Route

    @Serializable
    data object Profile : Route

    @Serializable
    data class PropertyDetail(val id: String) : Route

    @Serializable
    data object AiAssistant : Route

    @Serializable
    data class Booking(val propertyId: String) : Route

    @Serializable
    data object AdminDashboard : Route

    @Serializable
    data object AgentDashboard : Route

    @Serializable
    data object CreateListing : Route

    @Serializable
    data object ModerationQueue : Route

    @Serializable
    data object Messages : Route

    @Serializable
    data class ChatDetail(val userId: String) : Route

    @Serializable
    data class Payment(val propertyId: String, val amount: Double) : Route

    @Serializable
    data object EditProfile : Route
}
