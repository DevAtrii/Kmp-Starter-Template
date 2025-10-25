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

package com.kmpstarter.ui_utils.date_picker

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import com.kmpstarter.utils.kotlinx_datetime.fromLong
import com.kmpstarter.utils.kotlinx_datetime.today
import com.kmpstarter.utils.logging.Log
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.plus

private const val TAG = "SelectableDates"

@OptIn(ExperimentalMaterial3Api::class)
object PastOrPresentSelectableDates : SelectableDates {

    override fun isSelectableDate(utcTimeMillis: Long): Boolean {
        // Convert UTC milliseconds from DatePicker to LocalDate using UTC timezone
        val selectedDate = LocalDate.fromLong(utcTimeMillis, TimeZone.UTC)

        // Get today's date in UTC to match DatePicker's timezone
        val todayDate = LocalDate.today(TimeZone.UTC)
        
        // Allow one extra day (tomorrow) in addition to today and past dates
        val tomorrowDate = todayDate.plus(DatePeriod(days = 1))

        val isSelectable = selectedDate <= tomorrowDate
        
        Log.d(TAG, "Selected date: $selectedDate, Today: $todayDate, Tomorrow: $tomorrowDate, Selectable: $isSelectable")

        // Allow selection if the date is tomorrow, today, or in the past
        return isSelectable
    }

    override fun isSelectableYear(year: Int): Boolean {
        val currentYear = LocalDate.today().year
        return year <= currentYear
    }
}

