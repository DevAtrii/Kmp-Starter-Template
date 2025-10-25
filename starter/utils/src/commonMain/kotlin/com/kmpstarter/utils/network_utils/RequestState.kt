/*
 *
 *  *
 *  *  * Copyright (c) 2025
 *  *  *
 *  *  * Author: Athar Gul
 *  *  * GitHub: https://github.com/DevAtrii/Kmp-Starter-Template
 *  *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *  *
 *  *  * All rights reserved.
 *  *
 *  *
 *
 */

package com.kmpstarter.utils.network_utils

sealed class RequestState<out T> {
    data object Idle : RequestState<Nothing>()

    data object Loading : RequestState<Nothing>()

    data class Success<T>(val data: T) : RequestState<T>()

    data class Error(val message: String) : RequestState<Nothing>()

    fun isLoading() = this is Loading

    fun isSuccess() = this is Success

    fun isError() = this is Error

    /**
     * Returns data from a [Success].
     * @throws ClassCastException If the current state is not [Success]
     *  */
    fun getSuccessData() = (this as Success).data

    fun getSuccessDataOrNull(): T? {
        return try {
            (this as Success).data
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns an error message from an [Error]
     * @throws ClassCastException If the current state is not [Error]
     *  */
    fun getErrorMessage() = (this as Error).message

    fun getErrorMessageOrNull(): String? {
        return try {
            (this as Error).message
        } catch (e: Exception) {
            null
        }
    }


}

fun <T> RequestState<T>.handleState(
    onError: (String) -> Unit,
    onIdle: (() -> Unit)? = null,
    onSuccess: (T) -> Unit,
    onLoading: (() -> Unit),
) {
    when (this) {
        is RequestState.Error -> onError(this.message)
        RequestState.Idle -> onIdle?.invoke()
        RequestState.Loading -> onLoading()
        is RequestState.Success -> onSuccess(this.data)
    }
}
