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

package com.kmpstarter.ui_utils.popups.dialogs

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.kmpstarter.ui_utils.date_picker.PastOrPresentSelectableDates
import com.kmpstarter.ui_utils.theme.Dimens
import com.kmpstarter.utils.kotlinx_datetime.fromLong
import com.kmpstarter.utils.kotlinx_datetime.millis
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    selectedDate: LocalDate? = null,
    onDatePicked: (LocalDate) -> Unit = {},
    selectableDates: SelectableDates? = PastOrPresentSelectableDates,
    datePickerState: DatePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDate?.millis(TimeZone.UTC),
        selectableDates = selectableDates ?: DatePickerDefaults.AllDates
    ),
) {

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis ?: return@LaunchedEffect
        onDatePicked(LocalDate.fromLong(datePickerState.selectedDateMillis!!))
    }

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                if (datePickerState.selectedDateMillis != null) {
                    onDateSelected(LocalDate.fromLong(datePickerState.selectedDateMillis!!))
                    onDismiss()
                }
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(
            modifier = Modifier.padding(
                vertical = Dimens.paddingMedium
            ),
            state = datePickerState,
            title = null
        )
    }
}



















