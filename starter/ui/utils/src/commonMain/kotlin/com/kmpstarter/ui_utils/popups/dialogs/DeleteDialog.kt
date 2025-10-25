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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kmpstarter.ui_utils.common_composables.HorizontalSpacer
import com.kmpstarter.ui_utils.common_composables.VerticalSpacer
import com.kmpstarter.ui_utils.theme.Dimens

/**
 * Beautiful Material 3 Delete Dialog with iOS Cupertino design influence.
 * Features smooth animations, proper spacing, and intuitive user interaction.
 *
 * @param modifier Modifier for styling the dialog.
 * @param title The title text for the dialog (optional).
 * @param message The message text explaining what will be deleted.
 * @param confirmText The text for the confirm/delete button.
 * @param cancelText The text for the cancel button.
 * @param onConfirmDelete Callback when user confirms deletion.
 * @param onDismiss Callback when user dismisses the dialog.
 */
@Composable
fun DeleteDialog(
    modifier: Modifier = Modifier,
    title: String = "Delete Item",
    message: String = "Are you sure you want to delete this item? This action cannot be undone.",
    confirmText: String = "Delete",
    cancelText: String = "Cancel",
    confirmButtonEnabled: Boolean = true,
    cancelButtonEnabled: Boolean = true,
    onConfirmDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    BaseDialog(
        onDismiss = onDismiss,
    ) {
        DeleteDialogContent(
            modifier = Modifier,
            title = title,
            confirmButtonEnabled = confirmButtonEnabled,
            cancelButtonEnabled = cancelButtonEnabled,
            message = message,
            confirmText = confirmText,
            cancelText = cancelText,
            onConfirmDelete = onConfirmDelete,
            onDismissRequest = onDismiss
        )
    }
}

/**
 * Content for the Delete Dialog with Material 3 design and iOS Cupertino influence.
 *
 * @param modifier Modifier for styling the content.
 * @param title The title text for the dialog.
 * @param message The message text explaining what will be deleted.
 * @param confirmText The text for the confirm/delete button.
 * @param cancelText The text for the cancel button.
 * @param onConfirmDelete Callback when user confirms deletion.
 * @param onDismissRequest Callback when user dismisses the dialog.
 */
@Composable
private fun DeleteDialogContent(
    modifier: Modifier = Modifier,
    confirmButtonEnabled: Boolean = true,
    cancelButtonEnabled: Boolean = true,
    title: String,
    message: String,
    confirmText: String,
    cancelText: String,
    onConfirmDelete: () -> Unit,
    onDismissRequest: () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(Dimens.paddingMedium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        // Warning Icon
        Card(
            modifier = Modifier.size(64.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.errorContainer
            ),
            shape = RoundedCornerShape(32.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        VerticalSpacer(Dimens.paddingSmall)

        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        // Message
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(
                horizontal = Dimens.paddingMedium
            )
        )

        VerticalSpacer(Dimens.paddingMedium)

        // Buttons Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(Dimens.paddingSmall)
        ) {
            // Cancel Button
            OutlinedButton(
                enabled = cancelButtonEnabled,
                onClick = onDismissRequest,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                shape = RoundedCornerShape(Dimens.buttonRadius)
            ) {
                Text(
                    text = cancelText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }

            // Delete Button
            Button(
                enabled = confirmButtonEnabled,
                onClick = onConfirmDelete,
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                ),
                shape = RoundedCornerShape(Dimens.buttonRadius)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    modifier = Modifier.size(18.dp)
                )
                HorizontalSpacer(Dimens.paddingExtraSmall)
                Text(
                    text = confirmText,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

