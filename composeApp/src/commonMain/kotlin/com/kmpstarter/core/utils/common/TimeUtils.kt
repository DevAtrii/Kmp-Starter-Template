package com.kmpstarter.core.utils.common

import kotlinx.datetime.Clock

fun epochMillis(): Long = currentMillis()

fun currentMillis() = Clock.System.now().toEpochMilliseconds()

val currentMillis: Long get() = currentMillis()

fun hoursToMillis(hour: Int) = hour * 60 * 60 * 1000