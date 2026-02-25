/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

package com.kmpstarter.utils.data_classes

import kotlinx.serialization.Serializable

@Serializable
data class FieldState(
    val value: String = "",
    val error: String = "",
) {
    fun isValid() = !isEmpty() && error.isEmpty()

    fun isEmpty() = value.isEmpty()


    fun updateValue(newValue: String): FieldState {
        return copy(value = newValue)
    }

    fun updateError(newError: String): FieldState {
        return copy(error = newError)
    }

    fun reset() = FieldState()

}