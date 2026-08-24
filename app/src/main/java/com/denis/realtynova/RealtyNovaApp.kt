package com.denis.realtynova

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appfunctions.service.AppFunctionConfiguration
import com.denis.realtynova.core.ai.RealtyNovaFunctions
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class RealtyNovaApp : Application(), AppFunctionConfiguration.Provider {

    @Inject
    lateinit var realtyNovaFunctions: RealtyNovaFunctions

    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Enable Firebase Realtime Database Offline Persistence
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Ensure Crashlytics is capturing errors in release
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(!BuildConfig.DEBUG)

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
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override val appFunctionConfiguration: AppFunctionConfiguration by lazy {
        AppFunctionConfiguration.Builder()
            .addEnclosingClassFactory(RealtyNovaFunctions::class.java) { realtyNovaFunctions }
            .build()
    }
}
