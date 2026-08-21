package com.denis.realtynova.core.util

import com.denis.realtynova.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CloudinaryManager @Inject constructor() {
    private val apiKey = BuildConfig.REALTYNOVA_API_KEY

    suspend fun uploadImage(bytes: ByteArray): String? {
        // The API key is now accessible via BuildConfig.REALTYNOVA_API_KEY
        // Mock implementation
        return "https://res.cloudinary.com/demo/image/upload/sample.jpg"
    }
}
