package ru.mamykin.exchange.presentation

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

@Composable
internal fun TrackRecompositions(componentName: String, additionalInfo: String? = null) {
    val recompositionCount = remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        snapshotFlow { recompositionCount.intValue }
            .distinctUntilChanged()
            .onEach { count ->
                val message = if (additionalInfo != null) {
                    "$componentName for $additionalInfo recomposed $count times"
                } else {
                    "$componentName recomposed $count times"
                }
                Log.d("Performance", message)
            }
            .launchIn(this)
    }
    recompositionCount.intValue++
}