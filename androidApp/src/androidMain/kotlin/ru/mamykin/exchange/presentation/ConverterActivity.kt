package ru.mamykin.exchange.presentation

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.android.ext.android.inject
import ru.mamykin.exchange.AppNavigation

internal class ConverterActivity : AppCompatActivity() {

    private val viewModel: ConverterViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // val isDarkTheme = remember { mutableStateOf(appSettingsRepository.isNightThemeEnabled()) }
            // ConverterTheme(darkTheme = isDarkTheme.value) {
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