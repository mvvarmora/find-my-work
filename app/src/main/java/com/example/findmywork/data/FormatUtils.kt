package com.example.findmywork.data

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

fun formatInr(amount: Double): String {
    val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
    format.currency = Currency.getInstance("INR")
    format.minimumFractionDigits = if (amount == amount.toLong().toDouble()) 0 else 2
    format.maximumFractionDigits = 2
    return format.format(amount)
}

fun formatInr(amount: Int): String = formatInr(amount.toDouble())
