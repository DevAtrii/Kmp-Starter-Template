package com.kmpstarter.core.utils.common

expect fun epochMillis(): Long


fun hoursToMillis(hour:Int) = hour * 60 * 60 * 1000