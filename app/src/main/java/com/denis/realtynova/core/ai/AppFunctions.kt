package com.denis.realtynova.core.ai

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import com.denis.realtynova.core.domain.model.SearchFilter
import com.denis.realtynova.core.domain.repository.PropertyRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * A property result for search and listing.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PropertyResult(
    /** The unique identifier of the property */
    val id: String,
    /** The display title of the listing */
    val title: String,
    /** The formatted price of the property */
    val price: String,
    /** The general location (city, state) */
    val location: String,
    /** The type of property (e.g., Villa, Apartment) */
    val type: String
)

/**
 * Detailed information about a property.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class PropertyDetails(
    /** The unique identifier of the property */
    val id: String,
    /** The display title of the listing */
    val title: String,
    /** The long-form description of the property */
    val description: String,
    /** The formatted price of the property */
    val price: String,
    /** The exact street address */
    val address: String,
    /** Number of bedrooms */
    val bedrooms: Int,
    /** Number of bathrooms */
    val bathrooms: Double,
    /** Total area in square feet */
    val areaSqFt: Double,
    /** List of URLs for property images */
    val imageUrls: List<String>
)

/**
 * RealtyNova AppFunctions for AI agents.
 */
class RealtyNovaFunctions @Inject constructor(
    private val propertyRepository: PropertyRepository
) {

    /**
     * Searches for properties based on user criteria like location or keywords.
     * 
     * @param context The execution context.
     * @param query The search query (e.g., "villas in Miami").
     * @param maxPrice The maximum price filter in USD.
     * @return A list of matching properties with brief details.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun searchProperties(
        context: AppFunctionContext,
        query: String,
        maxPrice: Double? = null
    ): List<PropertyResult> = withContext(Dispatchers.IO) {
        propertyRepository.searchProperties(SearchFilter(query = query, maxPrice = maxPrice)).map {
            PropertyResult(
                id = it.id,
                title = it.title,
                price = "$${String.format(java.util.Locale.US, "%,.0f", it.price)}",
                location = it.location,
                type = it.type
            )
        }
    }

    /**
     * Retrieves full details for a specific property.
     * Required workflow: Call [searchProperties] first to find relevant property IDs.
     * 
     * @param context The execution context.
     * @param propertyId The unique ID of the property to inspect.
     * @return Detailed information about the property, or null if not found.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun getPropertyDetails(
        context: AppFunctionContext,
        propertyId: String
    ): PropertyDetails? = withContext(Dispatchers.IO) {
        propertyRepository.getPropertyById(propertyId)?.let {
            PropertyDetails(
                id = it.id,
                title = it.title,
                description = it.description,
                price = "$${String.format(java.util.Locale.US, "%,.0f", it.price)}",
                address = it.address,
                bedrooms = it.bedrooms,
                bathrooms = it.bathrooms,
                areaSqFt = it.areaSqFt,
                imageUrls = it.images.map { img -> img.url }
            )
        }
    }

    /**
     * Schedules a viewing appointment for a property.
     * 
     * @param context The execution context.
     * @param propertyId The unique ID of the property to view.
     * @param timestamp The preferred appointment time in milliseconds (epoch).
     * @return True if the request was successfully submitted.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun scheduleViewing(
        context: AppFunctionContext,
        propertyId: String,
        timestamp: Long
    ): Boolean = withContext(Dispatchers.IO) {
        propertyRepository.scheduleViewing(propertyId, timestamp)
    }

    /**
     * Checks the current real estate market trends.
     * Use this to provide insights on whether it's a good time to buy or sell.
     * 
     * @param context The execution context.
     * @return A string summary of the current market trend.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun checkMarketTrends(
        context: AppFunctionContext
    ): String = withContext(Dispatchers.IO) {
        val trends = propertyRepository.getMarketTrends()
        if (trends.last() > trends.first()) {
            "The market is currently trending upwards, showing a strong increase in property values over the last quarter."
        } else {
            "The market is currently stable with minor fluctuations in property values."
        }
    }

    /**
     * Sets an AI search alert for new listings.
     * The agent will notify the user when a property matching these criteria is found.
     * 
     * @param context The execution context.
     * @param query The search keywords (e.g., "modern beach house").
     * @param minPrice Minimum price in USD.
     * @param maxPrice Maximum price in USD.
     * @return True if the alert was successfully registered.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun setSearchAlert(
        context: AppFunctionContext,
        query: String,
        minPrice: Double? = null,
        maxPrice: Double? = null
    ): Boolean = withContext(Dispatchers.IO) {
        propertyRepository.setSearchAlert(SearchFilter(query = query, minPrice = minPrice, maxPrice = maxPrice))
    }
}
