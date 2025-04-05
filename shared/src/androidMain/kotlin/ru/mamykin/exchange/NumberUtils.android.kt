package ru.mamykin.exchange

actual fun formatNumber(number: Float): String {
    return "%.2f".format(number)
}