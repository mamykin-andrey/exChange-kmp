package ru.mamykin.exchange

sealed class AppScreen(val route: String) {

    data object Main : AppScreen("main")

    data object AppInfo : AppScreen("info")
}