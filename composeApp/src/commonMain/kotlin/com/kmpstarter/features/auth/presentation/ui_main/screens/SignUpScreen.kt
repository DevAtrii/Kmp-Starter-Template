package com.kmpstarter.features.auth.presentation.ui_main.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import com.kmpstarter.core.ui.layouts.loading.LoadingLayout
import com.kmpstarter.features.auth.presentation.events.AuthEvents
import com.kmpstarter.features.auth.presentation.ui_main.components.GoogleSignInButton
import com.kmpstarter.features.auth.presentation.ui_main.navigation.AuthScreens
import com.kmpstarter.features.auth.presentation.viewmodels.AuthViewModel
import com.kmpstarter.theme.Dimens
import dev.gitlive.firebase.auth.FirebaseUser
import kmpstarter.composeapp.generated.resources.Res
import org.koin.compose.koinInject
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.ExperimentalResourceApi
import kmpstarter.composeapp.generated.resources.*

@Composable
fun SignUpScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmPasswordVisible by remember { mutableStateOf(false) }

    val isSignUpEnabled = state.name.isNotEmpty() && state.email.isNotEmpty() &&
            state.password.isNotEmpty() && state.confirmPassword.isNotEmpty() && state.nameError.isEmpty() && state.emailError.isEmpty() &&
            state.passwordError.isEmpty() && state.confirmPasswordError.isEmpty()

    SignUpScreenContent(
        modifier = modifier,
        name = state.name,
        nameError = state.nameError,
        email = state.email,
        emailError = state.emailError,
        password = state.password,
        passwordError = state.passwordError,
        confirmPassword = state.confirmPassword,
        confirmPasswordError = state.confirmPasswordError,
        isPasswordVisible = isPasswordVisible,
        isConfirmPasswordVisible = isConfirmPasswordVisible,
        isSignUpEnabled = isSignUpEnabled,
        onNameChange = viewModel::onNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onPasswordVisibilityChange = { isPasswordVisible = it },
        onConfirmPasswordVisibilityChange = { isConfirmPasswordVisible = it },
        onGoogleSignedIn = { firebaseUser ->
            viewModel.onEvent(
                AuthEvents.OnSignedInWithGoogle(
                    firebaseUser = firebaseUser
                )
            )
        },
        onSignUpClick = {
            viewModel.onEvent(AuthEvents.SignUp)
        },
        onSignInClick = {
            viewModel.onEvent(
                AuthEvents.Navigate(
                    route = AuthScreens.SignIn
                )
            )
        }
    )

    if (state.isLoading) {
        LoadingLayout(
            modifier = modifier,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SignUpScreenContent(
    modifier: Modifier = Modifier,
    name: String,
    nameError: String,
    email: String,
    emailError: String,
    password: String,
    passwordError: String,
    confirmPassword: String,
    confirmPasswordError: String,
    isPasswordVisible: Boolean,
    isConfirmPasswordVisible: Boolean,
    isSignUpEnabled: Boolean,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    onConfirmPasswordVisibilityChange: (Boolean) -> Unit,
    onGoogleSignedIn: (FirebaseUser?) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { focusManager.clearFocus() }
            )
            .padding(Dimens.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        Spacer(modifier = Modifier.height(Dimens.paddingExtraLarge))

        // Header
        Text(
            text = stringResource(Res.string.auth_create_account),
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = stringResource(Res.string.auth_sign_in_to_continue),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(Dimens.paddingExtraLarge))

        // Name field
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text(stringResource(Res.string.auth_name_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null
                )
            },
            isError = nameError.isNotEmpty(),
            supportingText = if (nameError.isNotEmpty()) {
                { Text(nameError) }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Email field
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text(stringResource(Res.string.auth_email_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = null
                )
            },
            isError = emailError.isNotEmpty(),
            supportingText = if (emailError.isNotEmpty()) {
                { Text(emailError) }
            } else null,
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Password field
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(stringResource(Res.string.auth_password_label)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isPasswordVisible)
                            stringResource(Res.string.auth_hide_password)
                        else
                            stringResource(Res.string.auth_show_password)
                    )
                }
            },
            isError = passwordError.isNotEmpty(),
            supportingText = if (passwordError.isNotEmpty()) {
                { Text(passwordError) }
            } else null,
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Confirm Password field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = onConfirmPasswordChange,
            label = { Text(stringResource(Res.string.auth_confirm_password)) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null
                )
            },
            trailingIcon = {
                IconButton(onClick = { onConfirmPasswordVisibilityChange(!isConfirmPasswordVisible) }) {
                    Icon(
                        imageVector = if (isConfirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isConfirmPasswordVisible)
                            stringResource(Res.string.auth_hide_password)
                        else
                            stringResource(Res.string.auth_show_password)
                    )
                }
            },
            isError = confirmPasswordError.isNotEmpty(),
            supportingText = if (confirmPasswordError.isNotEmpty()) {
                { Text(confirmPasswordError) }
            } else null,
            singleLine = true,
            visualTransformation = if (isConfirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    if (isSignUpEnabled) onSignUpClick()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Sign Up Button
        Button(
            onClick = {
                onSignUpClick()
                focusManager.clearFocus()
            },
            enabled = isSignUpEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.paddingMedium)
        ) {
            Text(
                text = stringResource(Res.string.auth_sign_up),
                modifier = Modifier.padding(vertical = Dimens.paddingSmall)
            )
        }

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(Res.string.auth_or),
                modifier = Modifier.padding(horizontal = Dimens.paddingMedium),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        // Google Sign In
        var isGoogleLoading by rememberSaveable {
            mutableStateOf(false)
        }
        GoogleSignInButton(
            isLoading = isGoogleLoading,
            setIsLoading = { isGoogleLoading = it },
            onFirebaseResult = {

                onGoogleSignedIn(it.getOrNull())
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Sign In prompt
        Row(
            modifier = Modifier.padding(vertical = Dimens.paddingMedium),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.auth_already_have_account),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
            TextButton(onClick = onSignInClick) {
                Text(stringResource(Res.string.auth_sign_in))
            }
        }
    }
}

