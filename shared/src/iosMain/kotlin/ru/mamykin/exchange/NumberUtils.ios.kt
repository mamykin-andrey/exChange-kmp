package ru.mamykin.exchange

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter

actual fun formatNumber(number: Float): String {
    val formatter = NSNumberFormatter().apply {
        minimumFractionDigits = 2u
        maximumFractionDigits = 2u
    }
    return formatter.stringFromNumber(NSNumber(number)) ?: number.toString()
}