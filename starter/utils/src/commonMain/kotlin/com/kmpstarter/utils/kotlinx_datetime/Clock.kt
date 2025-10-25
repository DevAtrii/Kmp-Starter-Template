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

@file:OptIn(ExperimentalTime::class)

package com.kmpstarter.utils.kotlinx_datetime
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant


fun Clock.System.localDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime {
    val millis = Clock.System.now().toEpochMilliseconds()
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(timeZone)
}

fun Clock.System.currentInstant(
): Instant {
    val millis = Clock.System.now().toEpochMilliseconds()
    return Instant.fromEpochMilliseconds(millis)
}