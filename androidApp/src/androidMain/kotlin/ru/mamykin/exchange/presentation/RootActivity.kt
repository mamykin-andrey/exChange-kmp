package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import ru.mamykin.exchange.AppNavigation

internal class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme = isSystemInDarkTheme()
            LaunchedEffect(isDarkTheme) {
                updateStatusBar(isDarkTheme)
            }
            ConverterTheme(isDarkTheme) {
                AppNavigation()
            }
        }
    }

    private fun updateStatusBar(isDarkTheme: Boolean) {
        val statusBarColor = if (isDarkTheme) Color.Black else Color.White
        window.statusBarColor = statusBarColor.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = !isDarkTheme
    }
}