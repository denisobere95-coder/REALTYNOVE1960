package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.data.local.PropertyDao
import com.denis.realtynova.core.data.local.PropertyEntity
import com.denis.realtynova.core.domain.model.*
import com.denis.realtynova.core.domain.repository.PropertyRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val propertyDao: PropertyDao
) : PropertyRepository {

    private val propertiesCollection = firestore.collection("properties")

    override suspend fun getProperties(): List<Property> {
        // Try fetching from network and cache
        try {
            val firestoreProperties = propertiesCollection
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
                .toObjects(PropertyDto::class.java)
            
            if (firestoreProperties.isNotEmpty()) {
                val entities = firestoreProperties.map { it.toEntity() }
                propertyDao.clearAll()
                propertyDao.insertProperties(entities)
            }
        } catch (e: Exception) {
            Timber.e(e, "Firestore fetch failed, falling back to local cache")
        }

        // Return from local cache
        val cached = propertyDao.getAllProperties().first()
        return if (cached.isNotEmpty()) {
            cached.map { it.toDomain() }
        } else {
            getMockProperties()
        }
    }

    private fun getMockProperties(): List<Property> {
        return listOf(
            Property(
                id = "mock_1",
                title = "Skyline Penthouse Westlands",
                description = "Experience the height of luxury in this stunning 4-bedroom penthouse with panoramic city views.",
                price = 120000000.0,
                location = "Westlands, Nairobi",
                address = "Peponi Road",
                bedrooms = 4,
                bathrooms = 4.5,
                areaSqFt = 4200.0,
                images = listOf(PropertyImage("res:///drawable/img_45", PropertyImageType.HERO)),
                type = "Apartment",
                listingType = "Buy",
                isPremium = true,
                isVerified = true,
                yieldPercentage = 8.5,
                trustScore = TrustScore(98, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true, isAgentVerified = true, isDocumentsChecked = true)
            ),
            Property(
                id = "mock_2",
                title = "Emerald Garden Villa",
                description = "A serene escape in the heart of Karen, featuring expansive gardens and sustainable architecture.",
                price = 85000000.0,
                location = "Karen, Nairobi",
                address = "Karen Road",
                bedrooms = 5,
                bathrooms = 5.0,
                areaSqFt = 6500.0,
                images = listOf(PropertyImage("res:///drawable/img_53", PropertyImageType.HERO)),
                type = "Villa",
                listingType = "Buy",
                isPremium = true,
                isVerified = true,
                yieldPercentage = 7.2,
                trustScore = TrustScore(95, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true, isAgentVerified = true, isDocumentsChecked = true)
            ),
            Property(
                id = "mock_3",
                title = "Ocean Breeze Estate",
                description = "Modern coastal living with direct beach access and premium amenities.",
                price = 45000000.0,
                location = "Nyali, Mombasa",
                address = "Links Road",
                bedrooms = 3,
                bathrooms = 3.0,
                areaSqFt = 2800.0,
                images = listOf(PropertyImage("res:///drawable/img_33", PropertyImageType.HERO)),
                type = "Apartment",
                listingType = "Buy",
                isPremium = false,
                isVerified = true,
                yieldPercentage = 9.1,
                trustScore = TrustScore(92, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true, isAgentVerified = true)
            ),
            Property(
                id = "mock_4",
                title = "Lavington Heights",
                description = "Contemporary living in a secure gated community with close proximity to elite schools.",
                price = 350000.0,
                location = "Lavington, Nairobi",
                address = "James Gichuru Road",
                bedrooms = 3,
                bathrooms = 3.0,
                areaSqFt = 2200.0,
                images = listOf(PropertyImage("res:///drawable/img_42", PropertyImageType.HERO)),
                type = "Apartment",
                listingType = "Rent",
                isPremium = false,
                isVerified = true,
                trustScore = TrustScore(88, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true)
            ),
            Property(
                id = "mock_5",
                title = "Muthaiga Heritage Estate",
                description = "Classic elegance meets modern luxury in this historic Muthaiga residence.",
                price = 150000000.0,
                location = "Muthaiga, Nairobi",
                address = "Muthaiga Road",
                bedrooms = 6,
                bathrooms = 6.5,
                areaSqFt = 8000.0,
                images = listOf(PropertyImage("res:///drawable/img_51", PropertyImageType.HERO)),
                type = "Villa",
                listingType = "Buy",
                isPremium = true,
                isVerified = true,
                yieldPercentage = 6.8,
                trustScore = TrustScore(99, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true, isAgentVerified = true, isDocumentsChecked = true)
            )
        )
    }

    override suspend fun searchProperties(query: String, maxPrice: Double?): List<Property> {
        val allProperties = getProperties()
        return if (query.isBlank() && maxPrice == null) {
            allProperties
        } else {
            allProperties.filter {
                (query.isBlank() || it.title.contains(query, ignoreCase = true) || it.location.contains(query, ignoreCase = true)) &&
                (maxPrice == null || it.price <= maxPrice)
            }
        }
    }

    override suspend fun getPropertyById(id: String): Property? {
        val firestoreProperty = try {
            propertiesCollection.document(id).get().await()
                .toObject(PropertyDto::class.java)?.toDomain()
        } catch (e: Exception) {
            null
        }

        return firestoreProperty ?: getMockProperties().find { it.id == id }
    }

    override suspend fun scheduleViewing(propertyId: String, timestamp: Long): Boolean {
        return try {
            val viewing = hashMapOf(
                "propertyId" to propertyId,
                "timestamp" to timestamp,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("viewings").add(viewing).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error scheduling viewing")
            false
        }
    }

    override suspend fun getMarketTrends(): List<Float> {
        // In a real app, this could be fetched from a dedicated collection
        return listOf(0.2f, 0.4f, 0.35f, 0.6f, 0.55f, 0.8f, 0.75f, 0.9f)
    }

    override suspend fun setSearchAlert(query: String, minPrice: Double?, maxPrice: Double?): Boolean {
        return try {
            val alert = hashMapOf(
                "query" to query,
                "minPrice" to minPrice,
                "maxPrice" to maxPrice,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("searchAlerts").add(alert).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error setting search alert")
            false
        }
    }

    override suspend fun saveProperty(property: Property): Result<Unit> {
        return try {
            val dto = PropertyDto(
                id = property.id,
                title = property.title,
                description = property.description,
                price = property.price,
                currency = property.currency,
                location = property.location,
                address = property.address,
                bedrooms = property.bedrooms,
                bathrooms = property.bathrooms,
                areaSqFt = property.areaSqFt,
                images = property.images.map { 
                    PropertyImageDto(it.url, it.type.name, it.altText)
                },
                type = property.type,
                listingType = property.listingType,
                isVerified = property.isVerified,
                isPremium = property.isPremium,
                latitude = property.latitude,
                longitude = property.longitude,
                amenities = property.amenities,
                neighborhoodInfo = property.neighborhoodInfo,
                nearbyAmenities = property.nearbyAmenities.map {
                    NearbyAmenityDto(it.name, it.distanceKm, it.type)
                },
                yieldPercentage = property.yieldPercentage,
                appreciationRate = property.appreciationRate,
                createdAt = System.currentTimeMillis()
            )
            propertiesCollection.document(property.id).set(dto).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error saving property")
            Result.failure(e)
        }
    }

    override suspend fun getPendingListings(): List<Property> {
        return emptyList()
    }

    override suspend fun updateListingStatus(id: String, status: String): Boolean {
        return true
    }

    override suspend fun uploadFile(bucket: String, path: String, bytes: ByteArray): String {
        return "https://example.com/file.pdf"
    }
}

/**
 * Data Transfer Object for Property to match Firestore structure
 */
data class PropertyDto(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val currency: String = "KSh",
    val location: String = "",
    val address: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Double = 0.0,
    val areaSqFt: Double = 0.0,
    val images: List<PropertyImageDto> = emptyList(),
    val type: String = "",
    val listingType: String = "",
    val isVerified: Boolean = false,
    val isPremium: Boolean = false,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val amenities: List<String> = emptyList(),
    val neighborhoodInfo: String? = null,
    val nearbyAmenities: List<NearbyAmenityDto> = emptyList(),
    val yieldPercentage: Double? = null,
    val appreciationRate: Double? = null,
    val createdAt: Long = 0L
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
        images = images.map { it.toDomain() },
        type = type,
        listingType = listingType,
        isVerified = isVerified,
        isPremium = isPremium,
        latitude = latitude,
        longitude = longitude,
        amenities = amenities,
        neighborhoodInfo = neighborhoodInfo,
        nearbyAmenities = nearbyAmenities.map { it.toDomain() },
        yieldPercentage = yieldPercentage,
        appreciationRate = appreciationRate
    )

    fun toEntity(): PropertyEntity = PropertyEntity(
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
        images = images.map { it.toDomain() },
        amenities = amenities,
        type = type,
        listingType = listingType,
        isVerified = isVerified,
        isPremium = isPremium,
        latitude = latitude,
        longitude = longitude,
        neighborhoodInfo = neighborhoodInfo,
        yieldPercentage = yieldPercentage,
        appreciationRate = appreciationRate,
        createdAt = createdAt
    )
}

data class PropertyImageDto(
    val url: String = "",
    val type: String = "HERO",
    val altText: String? = null
) {
    fun toDomain(): PropertyImage = PropertyImage(
        url = url,
        type = try { PropertyImageType.valueOf(type) } catch (e: Exception) { PropertyImageType.HERO },
        altText = altText
    )
}

data class NearbyAmenityDto(
    val name: String = "",
    val distanceKm: Double = 0.0,
    val type: String = ""
) {
    fun toDomain(): NearbyAmenity = NearbyAmenity(
        name = name,
        distanceKm = distanceKm,
        type = type
    )
}
