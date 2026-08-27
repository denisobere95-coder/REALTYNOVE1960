package com.denis.realtynova

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.appfunctions.service.AppFunctionConfiguration
import com.denis.realtynova.core.ai.RealtyNovaFunctions
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.PersistentCacheSettings
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.remoteConfigSettings
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RealtyNovaApp : Application(), AppFunctionConfiguration.Provider {

    @Inject
    lateinit var realtyNovaFunctions: RealtyNovaFunctions

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Firebase
        FirebaseApp.initializeApp(this)
        
        // Initialize App Check for security (Play Integrity)
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
            PlayIntegrityAppCheckProviderFactory.getInstance(),
        )

        // Initialize Remote Config for dynamic UI control
        initializeRemoteConfig()

        // Optimize Firestore Performance
        configureFirestore()

        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Enable Firebase Realtime Database Offline Persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Ensure Crashlytics is capturing errors in release
        FirebaseCrashlytics.getInstance().isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG

        // Setup Notification Channels for FCM
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "property_alerts",
                "Property Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifications for price drops and property viewings"
            }
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun configureFirestore() {
        val settings = FirebaseFirestoreSettings.Builder()
            .setLocalCacheSettings(
                PersistentCacheSettings.newBuilder()
                    .setSizeBytes(100 * 1024 * 1024) // 100MB cache for fast offline access
                    .build()
            )
            .build()
        FirebaseFirestore.getInstance().firestoreSettings = settings
    }

    private fun initializeRemoteConfig() {
        val remoteConfig = FirebaseRemoteConfig.getInstance()
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = if (BuildConfig.DEBUG) 0 else 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        
        // Set default values
        val defaults = mapOf(
            "featured_property_id" to "mock_1",
            "is_maintenance_mode" to false,
            "promotion_banner_text" to "Welcome to RealtyNova C3"
        )
        remoteConfig.setDefaultsAsync(defaults)
        remoteConfig.fetchAndActivate()
    }

    override val appFunctionConfiguration: AppFunctionConfiguration by lazy {
        AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(RealtyNovaFunctions::class.java) { realtyNovaFunctions }
            .build()
    }
}
