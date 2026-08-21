package com.denis.realtynova.core.data.manager

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.denis.realtynova.core.domain.model.UserRole
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "realtynova_session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val USER_ROLE_KEY = stringPreferencesKey("user_role")
    private val ONBOARDING_COMPLETED_KEY = booleanPreferencesKey("onboarding_completed")
    private val BIOMETRIC_ENABLED_KEY = booleanPreferencesKey("biometric_enabled")
    private val SCREENSHOT_PREVENTION_KEY = booleanPreferencesKey("screenshot_prevention")

    val userRole: Flow<UserRole> = context.dataStore.data.map { preferences ->
        val roleName = preferences[USER_ROLE_KEY] ?: UserRole.BUYER.name
        UserRole.valueOf(roleName)
    }

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[ONBOARDING_COMPLETED_KEY] ?: false
    }

    val isBiometricEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BIOMETRIC_ENABLED_KEY] ?: false
    }

    val isScreenshotPreventionEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[SCREENSHOT_PREVENTION_KEY] ?: false
    }

    suspend fun setUserRole(role: UserRole) {
        context.dataStore.edit { preferences ->
            preferences[USER_ROLE_KEY] = role.name
        }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[ONBOARDING_COMPLETED_KEY] = completed
        }
    }

    suspend fun setBiometricEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BIOMETRIC_ENABLED_KEY] = enabled
        }
    }

    suspend fun setScreenshotPreventionEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[SCREENSHOT_PREVENTION_KEY] = enabled
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
