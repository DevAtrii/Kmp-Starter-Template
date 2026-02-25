/*
 *
 *  *
 *  *  * Copyright (c) 2026
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

package com.kmpstarter.ui_components.text_fields

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.kmpstarter.ui_components.text.ErrorText
import com.kmpstarter.utils.data_classes.FieldState

@Composable
fun FormPasswordTextField(
    modifier: Modifier = Modifier,
    value: FieldState,
    onValueChange: (String) -> Unit,
    label: String,
    leadingIcon: ImageVector = Icons.Default.Lock,
    trailingIcon: Pair<ImageVector, ImageVector> = Pair(
        first = Icons.Default.Visibility,
        second = Icons.Default.VisibilityOff
    ),
    imeAction: ImeAction = ImeAction.Go,
    clearFocusOnImeAction: Boolean = false,
    safeImeAction: Boolean = true,
    onImeAction: () -> Unit,
) = FormPasswordTextField(
    modifier = modifier,
    value = value.value,
    onValueChange = onValueChange,
    error = value.error,
    label = label,
    leadingIcon = leadingIcon,
    trailingIcon = trailingIcon,
    imeAction = imeAction,
    clearFocusOnImeAction = clearFocusOnImeAction,
    safeImeAction = safeImeAction,
    onImeAction = onImeAction
)

@Composable
fun FormPasswordTextField(
    modifier: Modifier = Modifier,
    value: String,
    onValueChange: (String) -> Unit,
    error: String,
    label: String,
    leadingIcon: ImageVector = Icons.Default.Lock,
    trailingIcon: Pair<ImageVector, ImageVector> = Pair(
        first = Icons.Default.Visibility,
        second = Icons.Default.VisibilityOff
    ),
    imeAction: ImeAction = ImeAction.Go,
    clearFocusOnImeAction: Boolean = false,
    safeImeAction: Boolean = true,
    onImeAction: () -> Unit,
) {
    val focusManager = LocalFocusManager.current

    var isPasswordVisible by rememberSaveable { mutableStateOf(false) }

    fun handleImeAction(expected: ImeAction) {
        if (imeAction != expected || error.isNotEmpty() || (safeImeAction && value.isEmpty()))
            return

        if (clearFocusOnImeAction)
            focusManager.clearFocus()

        onImeAction()
    }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        isError = error.isNotEmpty(),
        supportingText = {
            if (error.isNotEmpty()) {
                ErrorText(
                    textAlign = TextAlign.Start,
                    error = error
                )
            }
        },
        label = { Text(text = label) },
        singleLine = true,
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = "Leading Icon",
            )
        },
        trailingIcon = {
            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                AnimatedContent(
                    targetState = isPasswordVisible,
                    transitionSpec = {
                        fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                    },
                    label = "visibility-animation",
                ) { isVisible ->
                    Icon(
                        imageVector = if (isVisible) trailingIcon.second else trailingIcon.first,
                        contentDescription = if (isVisible) "Hide Password" else "Show Password",
                    )
                }

            }
        },
        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
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
                .animateContentSize()
    )
}