package com.denis.realtynova.core.domain.model

enum class PropertyImageType {
    HERO, EXTERIOR, INTERIOR, KITCHEN, BEDROOM, BATHROOM, AMENITY, AERIAL, FLOOR_PLAN
}

data class PropertyImage(
    val url: String,
    val type: PropertyImageType,
    val altText: String? = null
)

data class NearbyAmenity(
    val name: String,
    val distanceKm: Double,
    val type: String
)

data class HouseDetails(
    val bedrooms: Int,
    val bathrooms: Double,
    val parkingSpaces: Int,
    val floors: Int,
    val builtAreaSqFt: Double,
    val landAreaSqFt: Double,
    val yearBuilt: Int?,
    val tenureType: String?
)

data class ApartmentDetails(
    val bedrooms: Int,
    val bathrooms: Double,
    val floorNumber: Int,
    val totalFloors: Int,
    val isFurnished: Boolean,
    val serviceCharge: Double?
)

data class LandDetails(
    val sizeAcres: Double,
    val parcelNumber: String,
    val zoningType: String,
    val topography: String?,
    val soilType: String?,
    val tenureType: String?,
    val isServiced: Boolean
)

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val currency: String = "KSh",
    val location: String,
    val address: String,
    val bedrooms: Int,
    val bathrooms: Double,
    val areaSqFt: Double,
    val images: List<PropertyImage>,
    val type: String,
    val listingType: String,
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val amenities: List<String> = emptyList(),
    val neighborhoodInfo: String? = null,
    val nearbyAmenities: List<NearbyAmenity> = emptyList(),
    val yieldPercentage: Double? = null,
    val appreciationRate: Double? = null,
    val trustScore: TrustScore? = null,
    val houseDetails: HouseDetails? = null,
    val apartmentDetails: ApartmentDetails? = null,
    val landDetails: LandDetails? = null
)
