package com.denis.realtynova.core.util

import android.content.Context
import android.content.Intent
import com.denis.realtynova.core.domain.model.Property
import com.denis.realtynova.core.navigation.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareManager @Inject constructor() {

    fun shareProperty(context: Context, property: Property) {
        val shareLink = "https://realtynova.com/property/${property.id}"
        val text = """
            Check out this property on RealtyNova!
            
            ${property.title}
            Location: ${property.location}
            Price: ${property.currency} ${String.format("%,.0f", property.price)}
            
            View more: $shareLink
        """.trimIndent()

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Luxury Property: ${property.title}")
            putExtra(Intent.EXTRA_TEXT, text)
        }
        
        context.startActivity(Intent.createChooser(intent, "Share Property"))
    }
}
