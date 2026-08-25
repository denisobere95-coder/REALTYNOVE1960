package com.denis.realtynova.core.domain.repository

import com.denis.realtynova.core.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    
    suspend fun loginWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(email: String, password: String, phoneNumber: String, displayName: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithFacebook(token: String): Result<User>
    suspend fun signInWithPhone(phoneNumber: String, verificationId: String, otp: String): Result<User>
    suspend fun logout()
    suspend fun resetPassword(email: String): Result<Unit>
    suspend fun deleteAccount(): Result<Unit>
    suspend fun updateUser(user: User): Result<Unit>
}
