package com.denis.realtynova.core.domain.model

data class User(
    val id: String,
    val email: String?,
    val phoneNumber: String?,
    val displayName: String?,
    val photoUrl: String?,
    val role: UserRole = UserRole.BUYER
)

enum class UserRole {
    BUYER, RENTER, SELLER, LANDLORD, AGENT, DEVELOPER, PROPERTY_MANAGER, INVESTOR, ADMIN
}
