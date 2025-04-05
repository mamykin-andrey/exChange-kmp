package ru.mamykin.exchange

actual fun logDebug(tag: String, message: String) {
    println("$tag: $message")
}