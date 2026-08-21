package com.denis.realtynova.core.util

import android.content.Context
import androidx.biometric.BiometricManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor() {
    fun isBiometricAvailable(context: Context): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS
    }
}
