package ru.mamykin.exchange

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

fun <T : Any?> Flow<T>.subscribeClosable(scope: CoroutineScope, onEach: (T) -> Unit): Closeable {
    val job = scope.launch {
        collect {
            onEach(it)
        }
    }
    return object : Closeable {
        override fun onClose() {
            job.cancel()
        }
    }
}