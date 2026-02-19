package com.kmpstarter.ui_components.text_fields

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.kmpstarter.ui_components.text.ErrorText
import com.kmpstarter.utils.data_classes.FieldState

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: FieldState,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Next,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    clearFocusOnImeAction: Boolean = false,
    safeImeAction: Boolean = true,
    onImeAction: () -> Unit,
) = FormTextField(
    modifier = modifier,
    value = value.value,
    onValueChange = onValueChange,
    error = value.error,
    label = label,
    leadingIcon = leadingIcon,
    imeAction = imeAction,
    maxLines = maxLines,
    singleLine = singleLine,
    readOnly = readOnly,
    clearFocusOnImeAction = clearFocusOnImeAction,
    safeImeAction = safeImeAction,
    onImeAction = onImeAction,
)

@Composable
fun FormTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    error: String,
    label: String,
    leadingIcon: ImageVector? = null,
    imeAction: ImeAction = ImeAction.Next,
    maxLines: Int = 1,
    singleLine: Boolean = true,
    readOnly: Boolean = false,
    clearFocusOnImeAction: Boolean = false,
    safeImeAction: Boolean = true,
    onImeAction: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    fun handleImeAction(expected: ImeAction) {
        if (imeAction != expected || error.isNotEmpty() || (safeImeAction && value.isEmpty()))
            return

        if (clearFocusOnImeAction)
            focusManager.clearFocus()

        onImeAction()
    }

    val colors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = if (readOnly) MaterialTheme.colorScheme.outline else Color.Unspecified,
        unfocusedTextColor = if (readOnly) MaterialTheme.colorScheme.outline else Color.Unspecified,
    )
    val leadingIconComposable: @Composable (() -> Unit)? = if (leadingIcon != null) {
        {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Leading Icon",
            )
        }
    } else null

    OutlinedTextField(
        value = value,
        readOnly = readOnly,
        onValueChange = onValueChange,
        isError = error.isNotEmpty(),
        colors = colors,
        supportingText = {
            if (error.isNotEmpty()) {
                ErrorText(
                    textAlign = TextAlign.Start,
                    error = error
                )
            }
        },
        label = {
            Text(text = label)
        },
        maxLines = maxLines,
        singleLine = singleLine,
        leadingIcon = leadingIconComposable,
        keyboardOptions =
            KeyboardOptions(
                imeAction = imeAction,
            ),
        keyboardActions =
            KeyboardActions(
                onDone = { handleImeAction(expected = ImeAction.Done) },
                onNext = { handleImeAction(expected = ImeAction.Next) },
                onGo = { handleImeAction(expected = ImeAction.Go) },
            ),
        modifier =
            modifier
                .fillMaxWidth()
                .animateContentSize(),
    )
}