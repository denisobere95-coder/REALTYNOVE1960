package com.denis.realtynova.core.domain.repository

import com.denis.realtynova.core.domain.model.Property

interface PropertyRepository {
    suspend fun getProperties(): List<Property>
    suspend fun searchProperties(query: String, maxPrice: Double?): List<Property>
    suspend fun getPropertyById(id: String): Property?
    suspend fun scheduleViewing(propertyId: String, timestamp: Long): Boolean
    suspend fun getMarketTrends(): List<Float>
    suspend fun setSearchAlert(query: String, minPrice: Double?, maxPrice: Double?): Boolean
    suspend fun saveProperty(property: Property): Result<Unit>
    suspend fun getPendingListings(): List<Property>
    suspend fun updateListingStatus(id: String, status: String): Boolean
    suspend fun uploadFile(bucket: String, path: String, bytes: ByteArray): String
}
