package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import ru.mamykin.exchange.AppNavigation

internal class RootActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.White.toArgb()
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
        setContent {
            // val isDarkTheme = isSystemInDarkTheme()
            ConverterTheme {
                AppNavigation()
                // { isDarkTheme.value = it }
            }
        }
    }
}