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

package com.kmpstarter.core.utils.common


import kotlin.time.Clock
import kotlin.time.ExperimentalTime


fun epochMillis(): Long = currentMillis()

@OptIn(ExperimentalTime::class)
fun currentMillis() = Clock.System.now().toEpochMilliseconds()

val currentMillis: Long get() = currentMillis()

fun hoursToMillis(hour: Int) = hour * 60 * 60 * 1000