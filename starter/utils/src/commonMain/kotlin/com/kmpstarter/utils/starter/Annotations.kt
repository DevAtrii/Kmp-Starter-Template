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

package com.kmpstarter.utils.starter

@Suppress("ExperimentalAnnotationRetention")
@RequiresOptIn(
    level = RequiresOptIn.Level.ERROR,
    message = "This API is experimental and may change in future versions, bugs or crashes can occur please report on Github"
)
@Retention(AnnotationRetention.BINARY) // Corrected: Ensures enforcement across module boundaries
@Target(
    AnnotationTarget.CLASS,
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY,
    AnnotationTarget.CONSTRUCTOR
)
annotation class ExperimentalStarterApi
