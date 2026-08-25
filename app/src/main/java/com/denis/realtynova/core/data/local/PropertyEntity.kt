package com.denis.realtynova.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.denis.realtynova.core.domain.model.*

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String,
    val location: String,
    val address: String,
    val bedrooms: Int,
    val bathrooms: Double,
    val areaSqFt: Double,
    val images: List<PropertyImage>,
    val amenities: List<String>,
    val type: String,
    val listingType: String,
    val isVerified: Boolean,
    val isPremium: Boolean,
    val latitude: Double,
    val longitude: Double,
    val neighborhoodInfo: String?,
    val yieldPercentage: Double?,
    val appreciationRate: Double?,
    val createdAt: Long
) {
    fun toDomain(): Property = Property(
        id = id,
        title = title,
        description = description,
        price = price,
        currency = currency,
        location = location,
        address = address,
        bedrooms = bedrooms,
        bathrooms = bathrooms,
        areaSqFt = areaSqFt,
        images = images,
        amenities = amenities,
        type = type,
        listingType = listingType,
        isVerified = isVerified,
        isPremium = isPremium,
        latitude = latitude,
        longitude = longitude,
        neighborhoodInfo = neighborhoodInfo,
        yieldPercentage = yieldPercentage,
        appreciationRate = appreciationRate
    )
}
