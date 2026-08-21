package com.denis.realtynova.core.util

object PaymentUtils {
    fun getCardBrand(number: String): String {
        return when {
            number.startsWith("4") -> "Visa"
            number.startsWith("5") -> "MasterCard"
            else -> "Credit Card"
        }
    }
}
