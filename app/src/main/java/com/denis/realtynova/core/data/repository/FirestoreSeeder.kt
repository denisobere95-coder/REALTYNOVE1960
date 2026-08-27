package com.denis.realtynova.core.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import timber.log.Timber

class FirestoreSeeder @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    suspend fun seedInitialData() {
        val properties = listOf(
            // Karen Villa
            PropertyDto(
                id = "1",
                title = "The Amber Villa — Karen",
                description = "A masterpiece of contemporary architecture, this 5-bedroom villa in Karen offers an unparalleled living experience.",
                price = 185000000.0,
                location = "Karen, Nairobi",
                address = "Miotoni Road, Karen",
                bedrooms = 5,
                bathrooms = 6.0,
                areaSqFt = 8200.0,
                images = listOf(
                    PropertyImageDto("https://images.unsplash.com/photo-1613490493576-7fde63acd811", "HERO"),
                    PropertyImageDto("https://images.unsplash.com/photo-1600210492486-724fe5c67fb0", "INTERIOR")
                ),
                type = "Villa",
                listingType = "Buy",
                isVerified = true,
                isPremium = true,
                latitude = -1.3201,
                longitude = 36.7124,
                amenities = listOf("Infinity Pool", "Private Gym", "Smart Home"),
                createdAt = System.currentTimeMillis()
            ),
            // Westlands Penthouse
            PropertyDto(
                id = "2",
                title = "Skyline Penthouse — Westlands",
                description = "Perched on the 22nd floor, this duplex penthouse offers 360-degree views of the Nairobi skyline.",
                price = 450000.0,
                location = "Westlands, Nairobi",
                address = "Rhapta Road, Westlands",
                bedrooms = 3,
                bathrooms = 3.5,
                areaSqFt = 3500.0,
                images = listOf(
                    PropertyImageDto("https://images.unsplash.com/photo-1512918766671-ad651ec30730", "HERO")
                ),
                type = "Apartment",
                listingType = "Rent",
                isVerified = true,
                isPremium = true,
                latitude = -1.2654,
                longitude = 36.8045,
                amenities = listOf("Rooftop Terrace", "Heated Pool"),
                createdAt = System.currentTimeMillis()
            )
        )

        try {
            val batch = firestore.batch()
            properties.forEach { property ->
                val docRef = firestore.collection("properties").document(property.id)
                batch.set(docRef, property)
            }
            batch.commit().await()
            Timber.d("Initial data seeded successfully")
        } catch (e: Exception) {
            Timber.e(e, "Error seeding initial data")
        }
    }
}
