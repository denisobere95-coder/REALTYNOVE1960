package com.denis.realtynova.core.domain.repository

import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.domain.model.SearchFilter

interface PropertyRepository {
    suspend fun getProperties(limit: Int = 20, lastVisibleId: String? = null): List<Property>
    suspend fun searchProperties(filter: SearchFilter, limit: Int = 20): List<Property>
    suspend fun getPropertyById(id: String): Property?
    suspend fun scheduleViewing(propertyId: String, timestamp: Long): Boolean
    suspend fun getMarketTrends(): List<Float>
    suspend fun setSearchAlert(filter: SearchFilter): Boolean
    suspend fun saveProperty(property: Property): Result<Unit>
    suspend fun getPendingListings(): List<Property>
    suspend fun updateListingStatus(id: String, status: String): Boolean
    suspend fun uploadFile(bucket: String, path: String, bytes: ByteArray): String
    suspend fun reportProperty(propertyId: String, reason: String, description: String): Result<Unit>
    fun getFeaturedPropertyId(): String
}
