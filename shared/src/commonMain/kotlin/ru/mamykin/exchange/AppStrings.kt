package ru.mamykin.exchange

/**
 * Holder for all strings in the app shared between platforms.
 * Currently doesn't support localization.
 */
object AppStrings {
    const val CONVERTER_TITLE = "Rates & conversions"
    const val ABOUT_TITLE = "About"
    const val ERROR_NETWORK_TITLE = "Unable to load data, please try again"
    const val ERROR_NETWORK_RETRY = "Retry"
    const val ABOUT_TEXT =
        "Currency exchange rates Kotlin Multiplatform mobile app (Android & iOS). \nClean architecture with shared code for data, domain and presentation layers.\nThe UI is implemented with Jetpack Compose on Android and Swift UI on iOS."
    const val ABOUT_OPEN_GITHUB_TITLE = "See the project on GitHub"
    const val ABOUT_GITHUB_URL = "https://github.com/mamykin-andrey/exChange-kmp"
}