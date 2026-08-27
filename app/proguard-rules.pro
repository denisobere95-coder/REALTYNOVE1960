# REALTYNOVA — PRODUCTION PROGUARD RULES
#
# Optimized for:
# 1. Firebase (Auth, Firestore, Storage)
# 2. Hilt / Dagger
# 3. Room
# 4. Coil
# 5. Gemini / Vertex AI

# --- Firebase ---
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }

# --- Firestore DTOs (Data Transfer Objects) ---
# We must keep these so Firestore can use reflection to map documents to classes.
-keep class com.denis.realtynova.core.data.repository.PropertyDto { *; }
-keep class com.denis.realtynova.core.data.repository.PropertyImageDto { *; }
-keep class com.denis.realtynova.core.data.repository.NearbyAmenityDto { *; }
-keep class com.denis.realtynova.core.data.repository.UserDto { *; }

# --- Kotlin Serialization ---
-keepattributes *Annotation*, InnerClasses
-keep class kotlinx.serialization.json.** { *; }
-keepclassmembers class com.denis.realtynova.core.navigation.Route** {
    *** Companion;
}

# --- Hilt ---
-keep class dagger.hilt.android.internal.** { *; }

# --- Timber ---
-keep class timber.log.Timber* { *; }
-dontwarn timber.log.Timber**

# --- UI / Compose ---
-keep class androidx.compose.ui.platform.AndroidComposeView { *; }
