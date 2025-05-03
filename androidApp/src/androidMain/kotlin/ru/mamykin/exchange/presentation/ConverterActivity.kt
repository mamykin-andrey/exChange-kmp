package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import org.koin.android.ext.android.inject
import ru.mamykin.exchange.AppNavigation

internal class ConverterActivity : AppCompatActivity() {

    private val viewModel: ConverterViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            // val isDarkTheme = isSystemInDarkTheme()
            SideEffect {
                window.statusBarColor = Color.White.toArgb()
                WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true
            }
            ConverterTheme {
                val state by viewModel.stateFlow.collectAsState()
                AppNavigation(
                    state = state,
                    onIntent = viewModel::onIntent,
                    effectFlow = viewModel.effectFlow,
                )
                // { isDarkTheme.value = it }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.startRatesLoading()
    }

    override fun onStop() {
        super.onStop()
        viewModel.stopRatesLoading()
    }
}