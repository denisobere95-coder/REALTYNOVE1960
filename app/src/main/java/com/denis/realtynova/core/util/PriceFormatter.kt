package com.denis.realtynova.core.util

import java.text.NumberFormat
import java.util.Locale

object PriceFormatter {
    fun formatPrice(price: Double, currency: String = "KSh"): String {
        val formatter = NumberFormat.getCurrencyInstance(Locale("en", "KE"))
        // Manually adjust currency symbol if needed or use the one from locale
        val formatted = formatter.format(price)
        return formatted.replace("KES", currency).replace("Ksh", currency)
    }
}
