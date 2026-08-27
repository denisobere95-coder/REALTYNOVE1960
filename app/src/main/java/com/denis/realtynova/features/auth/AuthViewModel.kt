package com.denis.realtynova.features.auth

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denis.realtynova.core.data.manager.SessionManager
import com.denis.realtynova.core.domain.model.AuthState
import com.denis.realtynova.core.domain.model.User
import com.denis.realtynova.core.domain.model.UserRole
import com.denis.realtynova.core.domain.repository.AuthRepository
import com.denis.realtynova.core.util.GoogleAuthManager
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val sessionManager: SessionManager,
    private val googleAuthManager: GoogleAuthManager,
    private val firebaseAnalytics: FirebaseAnalytics,
) : ViewModel() {

    private val firebaseAuth = FirebaseAuth.getInstance()

    val authState: StateFlow<AuthState> = authRepository.authState
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthState.Initial)

    val currentUser: StateFlow<User?> = authRepository.currentUser
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        viewModelScope.launch {
            currentUser.collect { user ->
                if (user != null) {
                    authRepository.setOnlineStatus(isOnline = true)
                }
            }
        }
    }

    val userRole: StateFlow<UserRole> = sessionManager.userRole
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserRole.BUYER)

    val isOnboardingCompleted: StateFlow<Boolean> = sessionManager.isOnboardingCompleted
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isBiometricEnabled: StateFlow<Boolean> = sessionManager.isBiometricEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val isScreenshotPreventionEnabled: StateFlow<Boolean> = sessionManager.isScreenshotPreventionEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private var _verificationId: String? = null
    private var _resendToken: PhoneAuthProvider.ForceResendingToken? = null

    fun completeOnboarding() {
        viewModelScope.launch {
            sessionManager.setOnboardingCompleted(true)
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setBiometricEnabled(enabled)
        }
    }

    fun setScreenshotPreventionEnabled(enabled: Boolean) {
        viewModelScope.launch {
            sessionManager.setScreenshotPreventionEnabled(enabled)
        }
    }

    fun login(email: String, password: String) {
        if (!authRepository.isFirebaseConfigured()) {
            _uiState.value = AuthUiState.Error(
                "Firebase is not properly configured. Check your google-services.json file."
            )
            return
        }
        
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (cleanPassword.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }

        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.loginWithEmail(cleanEmail, cleanPassword)
            _uiState.value = result.fold(
                onSuccess = { 
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                        param(FirebaseAnalytics.Param.METHOD, "email")
                    }
                    AuthUiState.Success(it) 
                },
                onFailure = {
                    val msg = it.message ?: "Authentication failed"
                    if (msg.contains("API", ignoreCase = true)) {
                        AuthUiState.Error("API Error: Identity Toolkit API might be disabled in Google Console.")
                    } else {
                        AuthUiState.Error(msg)
                    }
                },
            )
        }
    }

    fun signUp(name: String, email: String, password: String, phone: String, role: UserRole = UserRole.BUYER) {
        if (!authRepository.isFirebaseConfigured()) {
            _uiState.value = AuthUiState.Error("Firebase setup incomplete. Check project configuration.")
            return
        }

        val cleanName = name.trim()
        val cleanEmail = email.trim()
        val cleanPassword = password.trim()

        if (cleanName.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your full name.")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(cleanEmail).matches()) {
            _uiState.value = AuthUiState.Error("Please enter a valid email address.")
            return
        }
        if (cleanPassword.length < 6) {
            _uiState.value = AuthUiState.Error("Password must be at least 6 characters.")
            return
        }
        
        val normalizedPhone = normalizeKenyanPhoneNumber(phone) ?: phone
        
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signUpWithEmail(cleanEmail, cleanPassword, normalizedPhone, cleanName)
            _uiState.value = result.fold(
                onSuccess = { 
                    setUserRole(role)
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
                        param(FirebaseAnalytics.Param.METHOD, "email")
                    }
                    AuthUiState.Success(it) 
                },
                onFailure = { 
                    val msg = it.message ?: "Signup failed"
                    if (msg.contains("API", ignoreCase = true)) {
                        AuthUiState.Error("API Restriction: The provided API key does not support Firebase Auth.")
                    } else {
                        AuthUiState.Error(msg)
                    }
                },
            )
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val idToken = googleAuthManager.signIn(context)
            if (idToken != null) {
                val result = authRepository.signInWithGoogle(idToken)
                _uiState.value = result.fold(
                    onSuccess = { 
                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
                            param(FirebaseAnalytics.Param.METHOD, "google")
                        }
                        AuthUiState.Success(it) 
                    },
                    onFailure = { AuthUiState.Error(it.message ?: "Google sign in failed") }
                )
            } else {
                _uiState.value = AuthUiState.Error("Google sign in cancelled or failed")
            }
        }
    }

    fun sendOtpCode(activity: Activity, phoneNumber: String) {
        val normalized = normalizeKenyanPhoneNumber(phoneNumber)
        if (normalized == null) {
            _uiState.value = AuthUiState.Error("Enter a valid Kenyan phone number.")
            return
        }

        _uiState.value = AuthUiState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationId = verificationId
                _resendToken = token
                _uiState.value = AuthUiState.CodeSent(normalized)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(normalized)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun resendOtpCode(activity: Activity, phoneNumber: String) {
        val normalized = normalizeKenyanPhoneNumber(phoneNumber) ?: return
        val token = _resendToken ?: return

        _uiState.value = AuthUiState.Loading

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                signInWithPhoneCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                _uiState.value = AuthUiState.Error(e.message ?: "Verification failed")
            }

            override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                _verificationId = verificationId
                _resendToken = token
                _uiState.value = AuthUiState.CodeSent(normalized)
            }
        }

        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(normalized)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .setForceResendingToken(token)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyOtp(phoneNumber: String, code: String) {
        val verificationId = _verificationId ?: return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.signInWithPhone(phoneNumber, verificationId, code)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success(it) },
                onFailure = { AuthUiState.Error(it.message ?: "Phone sign in failed") }
            )
        }
    }

    private fun signInWithPhoneCredential(credential: PhoneAuthCredential) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val result = firebaseAuth.signInWithCredential(credential).await()
                val firebaseUser = result.user
                if (firebaseUser != null) {
                    val user = User(
                        id = firebaseUser.uid,
                        email = firebaseUser.email,
                        phoneNumber = firebaseUser.phoneNumber,
                        displayName = firebaseUser.displayName,
                        photoUrl = firebaseUser.photoUrl?.toString()
                    )
                    _uiState.value = AuthUiState.Success(user)
                } else {
                    _uiState.value = AuthUiState.Error("User not found after sign in")
                }
            } catch (e: Exception) {
                Timber.e(e, "Phone sign in failed")
                _uiState.value = AuthUiState.Error(e.message ?: "Sign in failed")
            }
        }
    }

    private fun normalizeKenyanPhoneNumber(input: String): String? {
        val cleaned = input.trim().replace(" ", "").replace("-", "").replace("(", "").replace(")", "")
        if (cleaned.isBlank()) return null

        return when {
            // Already full E.164
            cleaned.startsWith("+254") -> if (cleaned.length == 13 && (cleaned.substring(4).all { it.isDigit() })) cleaned else null
            
            // Kenyan format starting with 254 (missing +)
            cleaned.startsWith("254") -> if (cleaned.length == 12 && (cleaned.substring(3).all { it.isDigit() })) "+$cleaned" else null
            
            // Standard Kenyan format starting with 0
            cleaned.startsWith("0") -> if (cleaned.length == 10 && (cleaned.all { it.isDigit() })) "+254${cleaned.substring(1)}" else null
            
            // 9-digit format (missing leading 0) - e.g. 712345678
            cleaned.length == 9 && cleaned.all { it.isDigit() } -> "+254$cleaned"
            
            // 7-digit format (very unlikely for Kenyan mobiles but possible for some landlines)
            cleaned.length == 7 && cleaned.all { it.isDigit() } -> "+254$cleaned"
            
            else -> null
        }
    }

    fun setVerificationId(id: String) {
        _verificationId = id
    }

    fun setUserRole(role: UserRole) {
        viewModelScope.launch {
            sessionManager.setUserRole(role)
            currentUser.value?.let { user ->
                authRepository.updateUser(user.copy(role = role))
            }
        }
    }

    fun resetPassword(email: String) {
        if (email.isBlank()) {
            _uiState.value = AuthUiState.Error("Please enter your email to reset password.")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.resetPassword(email)
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.SuccessMessage("Reset instructions sent to $email") },
                onFailure = { AuthUiState.Error(it.message ?: "Failed to send instructions") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            firebaseAnalytics.logEvent("logout", null)
            authRepository.logout()
            sessionManager.clearSession()
            _uiState.value = AuthUiState.Idle
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            val result = authRepository.deleteAccount()
            _uiState.value = result.fold(
                onSuccess = { 
                    sessionManager.clearSession()
                    AuthUiState.Idle 
                },
                onFailure = { AuthUiState.Error(it.message ?: "Account deletion failed") }
            )
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}

sealed interface AuthUiState {
    data object Idle : AuthUiState
    data object Loading : AuthUiState
    data class CodeSent(val phoneNumber: String) : AuthUiState
    data class Success(val user: User) : AuthUiState
    data class SuccessMessage(val message: String) : AuthUiState
    data class Error(val message: String) : AuthUiState
}
