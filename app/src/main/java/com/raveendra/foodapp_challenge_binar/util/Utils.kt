package com.raveendra.foodapp_challenge_binar.util

import java.text.NumberFormat
import java.util.Locale

fun Double.toIdrCurrency(): String {
    val numberFormat = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
    numberFormat.maximumFractionDigits = 0
    val formatted = numberFormat.format(this)
    val subStr = formatted.substring(2, formatted.length)
    return "Rp " + subStr.replace(",", ".")
}