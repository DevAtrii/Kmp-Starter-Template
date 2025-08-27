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

package com.kmpstarter.core.utils.kotlinx_datetime

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

fun Clock.System.localDateTime(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDateTime {
    val millis = Clock.System.now().toEpochMilliseconds()
    val instant = Instant.fromEpochMilliseconds(millis)
    return instant.toLocalDateTime(timeZone)
}