package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.domain.model.AuthState
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
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val analytics: FirebaseAnalytics,
    private val database: FirebaseDatabase,
) : AuthRepository {

    init {
        Timber.i("🚀 Welcome to RealtyNova C3! Connected to the future of Realty.")
    }

    private fun logAuthEvent(method: String) {
        val bundle = android.os.Bundle().apply {
            putString(FirebaseAnalytics.Param.METHOD, method)
        }
        analytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
    }

    override val authState: Flow<AuthState> = callbackFlow {
        var snapshotListener: com.google.firebase.firestore.ListenerRegistration? = null
        
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            
            snapshotListener?.remove()
            snapshotListener = null
            
            if (firebaseUser == null) {
                trySend(AuthState.Unauthenticated)
            } else {
                snapshotListener = firestore.collection("users").document(firebaseUser.uid)
                    .addSnapshotListener { snapshot, error ->
                        if (error != null) {
                            trySend(AuthState.Authenticated(firebaseUser.toDomainUser()))
                            return@addSnapshotListener
                        }
                        val user = snapshot?.toObject(UserDto::class.java)?.toDomain(firebaseUser.uid)
                            ?: firebaseUser.toDomainUser()
                        trySend(AuthState.Authenticated(user))
                    }
            }
        }
        
        firebaseAuth.addAuthStateListener(listener)
        
        awaitClose {
            firebaseAuth.removeAuthStateListener(listener)
            snapshotListener?.remove()
        }
    }

    override val currentUser: Flow<User?> = callbackFlow {
        var snapshotListener: com.google.firebase.firestore.ListenerRegistration? = null
        
        val listener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            
            // Remove previous snapshot listener if it exists
            snapshotListener?.remove()
            snapshotListener = null
            
            if (firebaseUser == null) {
                trySend(null)
            } else {
                snapshotListener = firestore.collection("users").document(firebaseUser.uid)
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
            snapshotListener?.remove()
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
            Timber.e(e, "Login failed")
            Result.failure(mapFirebaseError(e))
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String, phoneNumber: String, displayName: String): Result<User> {
        var firebaseUser: com.google.firebase.auth.FirebaseUser? = null
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            firebaseUser = result.user ?: throw Exception("User not found after signup")
            
            val profileUpdates = userProfileChangeRequest {
                this.displayName = displayName
            }
            firebaseUser.updateProfile(profileUpdates).await()
            
            val user = firebaseUser.toDomainUser().copy(
                displayName = displayName,
                phoneNumber = phoneNumber
            )
            
            // Atomic check: Save to Firestore
            saveUserToFirestore(user)
            
            Result.success(user)
        } catch (e: Exception) {
            Timber.e(e, "Signup failed")
            // Cleanup: Delete auth user if profile creation failed to prevent ghost accounts
            firebaseUser?.delete()?.await()
            Result.failure(mapFirebaseError(e))
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
        val dto = UserDto.fromDomain(user)
        firestore.collection("users").document(user.id).set(dto).await()
    }

    private fun mapFirebaseError(e: Exception): Exception {
        if (e !is FirebaseAuthException) return e
        
        val message = when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "That email address doesn't look right. Double check it."
            "ERROR_WRONG_PASSWORD" -> "The password you entered is incorrect. Try again."
            "ERROR_USER_NOT_FOUND" -> "We couldn't find an account with that email. Have you registered yet?"
            "ERROR_USER_DISABLED" -> "Your account has been temporarily disabled. Contact support."
            "ERROR_TOO_MANY_REQUESTS" -> "You've tried too many times! Please take a short break and try again."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already taken. Try signing in instead."
            "ERROR_WEAK_PASSWORD" -> "Your password is too simple. Use at least 6 characters."
            "ERROR_NETWORK_REQUEST_FAILED" -> "It seems you're offline. Check your internet connection."
            "ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL" -> "An account already exists with this email but using a different sign-in method."
            else -> e.localizedMessage ?: "Something went wrong. Please try again."
        }
        return Exception(message)
    }

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
            Timber.e(e, "Password reset failed: ${e.message}")
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
            // Sync to Realtime Database for presence/chat
            database.reference.child("users").child(user.id).updateChildren(
                mapOf(
                    "displayName" to user.displayName,
                    "photoUrl" to user.photoUrl,
                    "role" to user.role.name
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setOnlineStatus(isOnline: Boolean) {
        val uid = firebaseAuth.currentUser?.uid ?: return
        val userStatusRef = database.reference.child("status").child(uid)
        
        if (isOnline) {
            val onlineMap = mapOf(
                "state" to "online",
                "lastChanged" to ServerValue.TIMESTAMP
            )
            userStatusRef.setValue(onlineMap)
            userStatusRef.onDisconnect().setValue(
                mapOf(
                    "state" to "offline",
                    "lastChanged" to ServerValue.TIMESTAMP
                )
            )
        } else {
            userStatusRef.setValue(
                mapOf(
                    "state" to "offline",
                    "lastChanged" to ServerValue.TIMESTAMP
                )
            )
        }
    }

    override fun isFirebaseConfigured(): Boolean {
        return try {
            val app = com.google.firebase.FirebaseApp.getInstance()
            val options = app.options
            val hasKeys = options.apiKey.isNotEmpty() && options.applicationId.isNotEmpty()
            
            // Log for developer to check if they used the wrong key (Maps key)
            if (options.apiKey == "AIzaSyB_078J3na7VP2p1SbhC-fD0lJq3C536nw") {
                Timber.w("DEVELOPER WARNING: Your Firebase API Key looks like your Maps API Key. Auth might fail.")
            }
            
            hasKeys
        } catch (_: Exception) {
            false
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
    fun toDomain(id: String): User {
        val parsedRole = try {
            UserRole.valueOf(role.uppercase())
        } catch (_: Exception) {
            UserRole.BUYER
        }
        
        return User(
            id = id,
            email = email,
            phoneNumber = phoneNumber,
            displayName = displayName,
            photoUrl = photoUrl,
            role = parsedRole
        )
    }
    
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
