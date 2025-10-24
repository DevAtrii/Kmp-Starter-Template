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

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Month
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
fun LocalDate.Companion.fromLong(
    timestamp: Long,
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
): LocalDate {
    val instant = Instant.fromEpochMilliseconds(timestamp)
    // Use UTC for Material3 DatePicker compatibility
    return instant.toLocalDateTime(timeZone).date
}


@OptIn(ExperimentalTime::class)
fun LocalDate.millis(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) = this.toLocalDateTime().toInstant(timeZone).toEpochMilliseconds()

@OptIn(ExperimentalTime::class)
fun LocalDate.Companion.today(
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) = Clock.System.now()
    .toLocalDateTime(timeZone)
    .date


@OptIn(ExperimentalTime::class)
fun LocalDate.toLocalDateTime(
    hour: Int = Clock.System.localDateTime().hour,
    minute: Int = Clock.System.localDateTime().minute,
    second: Int = Clock.System.localDateTime().second,
    nanosecond: Int = Clock.System.localDateTime().nanosecond,
) = LocalDateTime(
    year = year,
    month = month,
    day = day,
    hour = hour,
    minute = minute,
    second = second,
    nanosecond = nanosecond
)


fun Month.length(year: Int): Int {
    val monthNumber = this.number
    val isLeapYear = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0))
    return when (monthNumber) {
        1, 3, 5, 7, 8, 10, 12 -> 31
        4, 6, 9, 11 -> 30
        2 -> if (isLeapYear) 29 else 28
        else -> throw IllegalArgumentException("Invalid month: $monthNumber")
    }
}





