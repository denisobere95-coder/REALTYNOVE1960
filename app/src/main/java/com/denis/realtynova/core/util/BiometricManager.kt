package com.denis.realtynova.core.util

import android.content.Context
import androidx.fragment.app.FragmentActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BiometricManager @Inject constructor() {
    
    fun isBiometricAvailable(context: Context): Boolean = false

    fun authenticate(
        activity: FragmentActivity,
        title: String = "RealtyNova Security",
        subtitle: String = "Authenticate to continue",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Mock success for local demo
        onSuccess()
    }
}
