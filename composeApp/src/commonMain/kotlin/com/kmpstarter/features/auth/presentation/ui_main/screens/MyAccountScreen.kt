package com.kmpstarter.features.auth.presentation.ui_main.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.kmpstarter.core.ui.layouts.loading.LoadingLayout
import com.kmpstarter.features.auth.presentation.events.AuthEvents
import com.kmpstarter.features.auth.presentation.ui_main.dialogs.ChangePasswordDialog
import com.kmpstarter.features.auth.presentation.ui_main.dialogs.DeleteAccountDialog
import com.kmpstarter.features.auth.presentation.viewmodels.AuthViewModel
import com.kmpstarter.theme.Dimens
import org.koin.compose.koinInject

@Composable
fun MyAccountScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = koinInject(),
) {
    val state by viewModel.state.collectAsState()

    // Delete Account Dialog State
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteAccountPassword by remember { mutableStateOf("") }
    var deleteAccountPasswordError by remember { mutableStateOf("") }
    var isDeletePasswordVisible by remember { mutableStateOf(false) }

    // Change Password Dialog State
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var currentPasswordError by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf("") }
    var confirmNewPassword by remember { mutableStateOf("") }
    var confirmNewPasswordError by remember { mutableStateOf("") }
    var isCurrentPasswordVisible by remember { mutableStateOf(false) }
    var isNewPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmNewPasswordVisible by remember { mutableStateOf(false) }
    var isChangePasswordLoading by remember { mutableStateOf(false) }

    MyAccountScreenContent(
        modifier = modifier,
        email = "user@example.com", // This will come from ViewModel
        showDeleteDialog = showDeleteDialog,
        deleteAccountPassword = deleteAccountPassword,
        deleteAccountPasswordError = deleteAccountPasswordError,
        isDeletePasswordVisible = isDeletePasswordVisible,
        onShowDeleteDialog = { showDeleteDialog = it },
        onDeletePasswordChange = {
            deleteAccountPassword = it
            deleteAccountPasswordError = ""
        },
        onDeletePasswordVisibilityChange = { isDeletePasswordVisible = it },
        onChangePasswordClick = { showChangePasswordDialog = true },
        onDeleteAccountConfirm = {
            if (deleteAccountPassword.length < 6) {
                deleteAccountPasswordError = "Password must be at least 6 characters"
                return@MyAccountScreenContent
            }
            /* Will be handled by ViewModel */
            showDeleteDialog = false
            deleteAccountPassword = ""
            deleteAccountPasswordError = ""
        },
        onSignOutClick = {
            viewModel.onEvent(AuthEvents.SignOut)
        }
    )

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            currentPassword = currentPassword,
            currentPasswordError = currentPasswordError,
            newPassword = newPassword,
            newPasswordError = newPasswordError,
            confirmNewPassword = confirmNewPassword,
            confirmNewPasswordError = confirmNewPasswordError,
            isCurrentPasswordVisible = isCurrentPasswordVisible,
            isNewPasswordVisible = isNewPasswordVisible,
            isConfirmNewPasswordVisible = isConfirmNewPasswordVisible,
            isLoading = isChangePasswordLoading,
            onCurrentPasswordChange = {
                currentPassword = it
                currentPasswordError = ""
            },
            onNewPasswordChange = {
                newPassword = it
                newPasswordError = ""
            },
            onConfirmNewPasswordChange = {
                confirmNewPassword = it
                confirmNewPasswordError = ""
            },
            onCurrentPasswordVisibilityChange = { isCurrentPasswordVisible = it },
            onNewPasswordVisibilityChange = { isNewPasswordVisible = it },
            onConfirmNewPasswordVisibilityChange = { isConfirmNewPasswordVisible = it },
            onDismiss = {
                showChangePasswordDialog = false
                currentPassword = ""
                newPassword = ""
                confirmNewPassword = ""
                currentPasswordError = ""
                newPasswordError = ""
                confirmNewPasswordError = ""
                isChangePasswordLoading = false
            },
            onConfirm = {
                var hasError = false
                if (currentPassword.length < 6) {
                    currentPasswordError = "Password must be at least 6 characters"
                    hasError = true
                }
                if (newPassword.length < 6) {
                    newPasswordError = "Password must be at least 6 characters"
                    hasError = true
                }
                if (newPassword != confirmNewPassword) {
                    confirmNewPasswordError = "Passwords do not match"
                    hasError = true
                }
                if (!hasError) {
                    isChangePasswordLoading = true
                    // Handle password change - will be handled by ViewModel
                }
            }
        )
    }

    // Delete Account Dialog
    if (showDeleteDialog) {
        DeleteAccountDialog(
            password = deleteAccountPassword,
            passwordError = deleteAccountPasswordError,
            isPasswordVisible = isDeletePasswordVisible,
            onPasswordChange = {},
            onPasswordVisibilityChange = { },
            onDismiss = { showDeleteDialog = false },
            onConfirm = {}
        )
    }

    if (state.isLoading) {
        LoadingLayout(
            modifier = modifier,
        )
    }
}

@Composable
private fun MyAccountScreenContent(
    modifier: Modifier = Modifier,
    email: String,
    showDeleteDialog: Boolean,
    deleteAccountPassword: String,
    deleteAccountPasswordError: String,
    isDeletePasswordVisible: Boolean,
    onShowDeleteDialog: (Boolean) -> Unit,
    onDeletePasswordChange: (String) -> Unit,
    onDeletePasswordVisibilityChange: (Boolean) -> Unit,
    onChangePasswordClick: () -> Unit,
    onDeleteAccountConfirm: () -> Unit,
    onSignOutClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(Dimens.paddingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Dimens.paddingMedium)
    ) {
        // Profile Icon
        Surface(
            modifier = Modifier.size(120.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier
                    .padding(24.dp)
                    .size(72.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(modifier = Modifier.height(Dimens.paddingLarge))

        // Email
        Text(
            text = email,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Account Options
        ElevatedCard (
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column {
                // Change Password Option
                ListItem(
                    headlineContent = { Text("Change Password") },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingContent = {
                        Icon(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier.clickable(onClick = onChangePasswordClick)
                )

                HorizontalDivider()

                // Delete Account Option
                ListItem(
                    headlineContent = {
                        Text(
                            "Delete Account",
                            color = MaterialTheme.colorScheme.error
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    },
                    modifier = Modifier.clickable { onShowDeleteDialog(true) }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Sign Out Button
        Button(
            onClick = onSignOutClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.errorContainer,
                contentColor = MaterialTheme.colorScheme.onErrorContainer
            )
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Sign Out",
                modifier = Modifier.padding(vertical = Dimens.paddingSmall)
            )
        }
    }
}

