package com.kmpstarter.feature_navigation.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


// this is temporary solution, try using the official solution
// the new solution is coming to nav3 runtime
// see: https://android-review.googlesource.com/c/platform/frameworks/support/+/3956084

@Stable
class ResultStore {

    private val mutex = Mutex()

    private val resultsMap = mutableMapOf<Any, Any?>()

    private val _changes =
        MutableSharedFlow<Any>(
            replay = 0,
            extraBufferCapacity = 1
        )

    /** Emits the key that changed */
    val changes: SharedFlow<Any> = _changes.asSharedFlow()

    @Suppress("UNCHECKED_CAST")
    fun <T> getResult(key: Any): T? =
        resultsMap[key] as? T

    suspend fun <T> setResult(key: Any, value: T) {
        mutex.withLock {
            resultsMap[key] = value
            _changes.emit(key)
        }
    }

    suspend fun removeResult(key: Any) {
        mutex.withLock {
            resultsMap.remove(key)
            _changes.emit(key)
        }
    }

    /** Observe updates for a specific key */
    fun <T> observeResult(key: Any): Flow<T> =
        changes
            .filter { it == key }
            .mapNotNull { getResult(key) }

    companion object {
        /**
         * Results should not survive process death
         * (navigation results are ephemeral)
         */
        val Saver = Saver<ResultStore, Unit>(
            save = { },
            restore = { ResultStore() }
        )
    }
}


@Suppress("ParamsComparedByRef")
@Composable
fun ProvideResultStore(
    resultStore: ResultStore,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalResultStore provides resultStore) {
        content()
    }
}


val LocalResultStore = compositionLocalOf<ResultStore> {
    error("Please provide ${ResultStore::class.qualifiedName}")
}

@Composable
fun rememberResultStore() = remember {
    ResultStore()
}