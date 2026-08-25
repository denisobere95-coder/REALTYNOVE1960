package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.User
import com.denis.realtynova.core.domain.model.UserRole
import com.denis.realtynova.core.domain.repository.AuthRepository
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics
) : AuthRepository {

    private fun logAuthEvent(method: String) {
        val bundle = android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            if (firebaseUser == null) {
                trySend(null)
            } else {
                // Fetch additional data from Firestore
                firestore.collection("users").document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(firebaseUser.toDomainUser())
                            return@addSnapshotListener
                        }
                        val user = snapshot?.toObject(UserDto::class.java)?.toDomain(firebaseUser.uid)
                            ?: firebaseUser.toDomainUser()
                        trySend(user)
                    }
            }
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
        }
    }

    override suspend fun loginWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User not found after login")
            
            val user = fetchUserFromFirestore(firebaseUser.uid) ?: firebaseUser.toDomainUser()
            logAuthEvent("email")
            Result.success(user)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Login failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, phoneNumber: String, displayName: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: throw Exception("User not found after signup")
            
            // Update profile with display name
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }
            firebaseUser.updateProfile(profileUpdates).await()
            
            val user = firebaseUser.toDomainUser().copy(
                displayName = displayName,
                phoneNumber = phoneNumber
            )
            
            // Save to Firestore
            saveUserToFirestore(user)
            
            Result.success(user)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Signup failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Google sign in failed")
            
            var user = fetchUserFromFirestore(firebaseUser.uid)
            if (user == null) {
                user = firebaseUser.toDomainUser()
                saveUserToFirestore(user)
            }
            logAuthEvent("google")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun fetchUserFromFirestore(uid: String): User? {
        return try {
            firestore.collection("users").document(uid).get().await()
                .toObject(UserDto::class.java)?.toDomain(uid)
        } catch (e: Exception) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
            null
        }
    }

    private suspend fun saveUserToFirestore(user: User) {
        try {
            val dto = UserDto.fromDomain(user)
            firestore.collection("users").document(user.id).set(dto).await()
        } catch (e: Exception) {
            com.google.firebase.crashlytics.FirebaseCrashlytics.getInstance().recordException(e)
        }
    }
// ...

    override suspend fun signInWithFacebook(token: String): Result<User> {
        return try {
            val credential = FacebookAuthProvider.getCredential(token)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Facebook sign in failed")
            
            var user = fetchUserFromFirestore(firebaseUser.uid)
            if (user == null) {
                user = firebaseUser.toDomainUser()
                saveUserToFirestore(user)
            }
            logAuthEvent("facebook")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithPhone(phoneNumber: String, verificationId: String, otp: String): Result<User> {
        return try {
            val credential = PhoneAuthProvider.getCredential(verificationId, otp)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("Phone sign in failed")
            
            var user = fetchUserFromFirestore(firebaseUser.uid)
            if (user == null) {
                user = firebaseUser.toDomainUser().copy(phoneNumber = phoneNumber)
                saveUserToFirestore(user)
            }
            logAuthEvent("phone")
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logout() {
        firebaseAuth.signOut()
    }

    override suspend fun resetPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            val bundle = android.os.Bundle().apply {
                putString(FirebaseAnalytics.Param.METHOD, "email")
            }
            analytics.logEvent("password_reset_sent", bundle)
            Result.success(Unit)
        } catch (e: Exception) {
            timber.log.Timber.e(e, "Password reset failed: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUser(user: User): Result<Unit> {
        return try {
            val dto = UserDto.fromDomain(user)
            firestore.collection("users").document(user.id).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun com.google.firebase.auth.FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email,
            phoneNumber = phoneNumber,
            displayName = displayName,
            photoUrl = photoUrl?.toString()
        )
    }
}

data class UserDto(
    val email: String? = null,
    val phoneNumber: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val role: String = "BUYER"
) {
    fun toDomain(id: String) = User(
        id = id,
        email = email,
        phoneNumber = phoneNumber,
        displayName = displayName,
        photoUrl = photoUrl,
        role = UserRole.valueOf(role)
    )
    
    companion object {
        fun fromDomain(user: User) = UserDto(
            email = user.email,
            phoneNumber = user.phoneNumber,
            displayName = user.displayName,
            photoUrl = user.photoUrl,
            role = user.role.name
        )
    }
}
