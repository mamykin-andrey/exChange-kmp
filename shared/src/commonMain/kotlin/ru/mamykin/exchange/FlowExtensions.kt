package ru.mamykin.exchange

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch

fun <T> Flow<T>.catchSafely(action: suspend FlowCollector<T>.(cause: Throwable) -> Unit): Flow<T> =
    catch { if (it is CancellationException) throw it else action(it) }