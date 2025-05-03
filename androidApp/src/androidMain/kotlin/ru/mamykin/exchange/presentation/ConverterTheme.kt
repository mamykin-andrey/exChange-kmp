package ru.mamykin.exchange.presentation

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
internal fun ConverterTheme(
    isNightTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (isNightTheme) darkColorScheme() else lightColorScheme(),
        content = {
            content()
        }
    )
}