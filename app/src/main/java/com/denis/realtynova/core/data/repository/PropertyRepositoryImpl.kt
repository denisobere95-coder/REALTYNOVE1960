package com.denis.realtynova.core.data.repository

import com.denis.realtynova.core.data.local.PropertyDao
import com.denis.realtynova.core.data.local.PropertyEntity
import com.denis.realtynova.core.domain.model.*
import com.denis.realtynova.core.domain.repository.PropertyRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton
import timber.log.Timber
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val storage: FirebaseStorage,
    private val remoteConfig: FirebaseRemoteConfig,
    private val propertyDao: PropertyDao,
) : PropertyRepository {

    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val propertiesCollection = firestore.collection("properties")

    override suspend fun getProperties(limit: Int, lastVisibleId: String?): List<Property> = withContext(Dispatchers.IO) {
        // Return from local cache immediately if available
        val cached = propertyDao.getAllProperties().firstOrNull() ?: emptyList()
        
        // Fetch from network in background to update cache
        repositoryScope.launch {
            try {
                var query: Query = propertiesCollection
                    .orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(limit.toLong())
                
                if (lastVisibleId != null) {
                    val lastDoc = propertiesCollection.document(lastVisibleId).get().await()
                    query = query.startAfter(lastDoc)
                }

                val firestoreProperties = query.get().await().toObjects(PropertyDto::class.java)
                
                if (firestoreProperties.isNotEmpty()) {
                    val entities = firestoreProperties.map { it.toEntity() }
                    if (lastVisibleId == null) propertyDao.clearAll() // Only clear on first page
                    propertyDao.insertProperties(entities)
                }
            } catch (e: Exception) {
                Timber.e(e, "Firestore fetch failed")
            }
        }

        if (cached.isNotEmpty()) {
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
                images = listOf(PropertyImage("https://images.unsplash.com/photo-1512917774080-9991f1c4c750?auto=format&fit=crop&w=800&q=80", PropertyImageType.HERO)),
                type = "Apartment",
                listingType = "Buy",
                isPremium = true,
                isVerified = true,
                latitude = -1.2633,
                longitude = 36.8016,
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
                images = listOf(PropertyImage("https://images.unsplash.com/photo-1613490493576-7fde63acd811?auto=format&fit=crop&w=800&q=80", PropertyImageType.HERO)),
                type = "Villa",
                listingType = "Buy",
                isPremium = true,
                isVerified = true,
                latitude = -1.3328,
                longitude = 36.7029,
                yieldPercentage = 7.2,
                trustScore = TrustScore(95, isOwnerVerified = true, isLocationVerified = true, isImagesVerified = true, isAgentVerified = true, isDocumentsChecked = true)
            )
        )
    }

    override suspend fun searchProperties(filter: SearchFilter, limit: Int): List<Property> = withContext(Dispatchers.IO) {
        try {
            var query: Query = propertiesCollection.limit(limit.toLong())
            
            // Apply server-side filters for performance
            filter.propertyType?.let { if (it != "All") query = query.whereEqualTo("type", it) }
            filter.listingType?.let { if (it != "All") query = query.whereEqualTo("listingType", it) }
            filter.bedrooms?.let { query = query.whereGreaterThanOrEqualTo("bedrooms", it) }
            
            if (filter.isVerified) {
                query = query.whereEqualTo("isVerified", true)
            }

            val snapshot = query.get().await()
            val remoteResults = snapshot.toObjects(PropertyDto::class.java).map { it.toDomain() }

            // Apply fine-grained local filtering
            var results = remoteResults.filter { property ->
                val matchesQuery = filter.query.isBlank() || 
                    property.title.contains(filter.query, ignoreCase = true) || 
                    property.location.contains(filter.query, ignoreCase = true)
                
                val matchesPrice = (filter.minPrice == null || property.price >= filter.minPrice) &&
                    (filter.maxPrice == null || property.price <= filter.maxPrice)
                
                val matchesAmenities = filter.amenities.isEmpty() || 
                    property.amenities.containsAll(filter.amenities)
                
                val matchesArea = filter.minArea == null || property.areaSqFt >= filter.minArea
                
                matchesQuery && matchesPrice && matchesAmenities && matchesArea
            }

            // Apply Sorting
            results = when (filter.sortBy) {
                SortOrder.PRICE_LOW_HIGH -> results.sortedBy { it.price }
                SortOrder.PRICE_HIGH_LOW -> results.sortedByDescending { it.price }
                SortOrder.NEWEST -> results // Remote query should handle this via createdAt if indexed
                SortOrder.RELEVANCE -> results
            }

            results
        } catch (e: Exception) {
            Timber.e(e, "Firestore search failed, falling back to cache")
            val cached = propertyDao.getAllProperties().firstOrNull() ?: emptyList()
            cached.asSequence().map { it.toDomain() }
                .filter { it.title.contains(filter.query, ignoreCase = true) }
                .toList()
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
        return listOf(0.2f, 0.4f, 0.35f, 0.6f, 0.55f, 0.8f, 0.75f, 0.9f)
    }

    override suspend fun setSearchAlert(filter: SearchFilter): Boolean {
        return try {
            val alert = hashMapOf(
                "filter" to filter,
                "userId" to (firebaseAuth.currentUser?.uid ?: ""),
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("searchAlerts").add(alert).await()
            true
        } catch (e: Exception) {
            Timber.e(e, "Error setting search alert")
            false
        }
    }

    override suspend fun reportProperty(propertyId: String, reason: String, description: String): Result<Unit> {
        return try {
            val report = hashMapOf(
                "propertyId" to propertyId,
                "reason" to reason,
                "description" to description,
                "reportedBy" to (firebaseAuth.currentUser?.uid ?: "anonymous"),
                "timestamp" to System.currentTimeMillis()
            )
            firestore.collection("reports").add(report).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
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

    override suspend fun getPendingListings(): List<Property> = emptyList()

    override suspend fun updateListingStatus(id: String, status: String): Boolean = true

    override suspend fun uploadFile(bucket: String, path: String, bytes: ByteArray): String = withContext(Dispatchers.IO) {
        try {
            val ref = storage.getReference(bucket).child(path)
            ref.putBytes(bytes).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            Timber.e(e, "Storage upload failed")
            ""
        }
    }

    override fun getFeaturedPropertyId(): String {
        return remoteConfig.getString("featured_property_id")
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
