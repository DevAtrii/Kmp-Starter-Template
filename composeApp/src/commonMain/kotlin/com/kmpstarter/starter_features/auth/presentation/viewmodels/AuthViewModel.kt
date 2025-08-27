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

package com.kmpstarter.starter_features.auth.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kmpstarter.core.events.controllers.SnackbarController
import com.kmpstarter.core.events.navigator.interfaces.Navigator
import com.kmpstarter.core.utils.network_utils.RequestState
import com.kmpstarter.starter_features.auth.domain.enums.SignInMethod
import com.kmpstarter.starter_features.auth.domain.models.UserData
import com.kmpstarter.starter_features.auth.domain.repository.AuthRepository
import com.kmpstarter.starter_features.auth.presentation.events.AuthEvents
import com.kmpstarter.starter_features.auth.presentation.states.AuthState
import dev.gitlive.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    private val navigator: Navigator,

    ) : ViewModel() {
    companion object {
        private const val MIN_PASSWORD_LENGTH = 6
        private const val MIN_NAME_LENGTH = 5
        private const val TAG = "AuthViewModel"
    }

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    // jobs
    private var signInJob: Job? = null
    private var signUpJob: Job? = null
    private var signOutJob: Job? = null
    private var forgetPasswordJob: Job? = null
    private var changePasswordJob: Job? = null
    private var deleteAccountJob: Job? = null
    private var checkIfAlreadyLoggedInJob: Job? = null

    fun onEvent(event: AuthEvents) {
        viewModelScope.launch {
            SnackbarController.sendAlert(null)
        }
        when (event) {
            AuthEvents.ChangePassword -> changePassword()
            AuthEvents.DeleteAccount -> deleteAccount()
            AuthEvents.ForgetPassword -> forgetPassword()
            AuthEvents.SignIn -> signIn()
            AuthEvents.SignOut -> signOut()
            AuthEvents.SignUp -> signUp()
            AuthEvents.ResetPasswordsInState ->
                _state.update {
                    it.copy(
                        password = "",
                        passwordError = "",
                        confirmPassword = "",
                        confirmPasswordError = "",
                    )
                }

            is AuthEvents.Navigate ->
                viewModelScope.launch {
                    navigator.navigateTo(
                        route = event.route,
                        popUpTo = event.popUpTo,
                        inclusive = event.inclusive,
                    )
                }

            is AuthEvents.NavigateOutsideFeature ->
                viewModelScope.launch {
                    navigator.navigateTo(
                        route = event.route,
                        popUpTo = event.popUpTo,
                        inclusive = event.inclusive,
                    )
                }

            is AuthEvents.StartSignInWithGoogle -> {
                _state.update {
                    it.copy(
                        user =
                            UserData(
                                method = SignInMethod.GOOGLE,
                            ),
                    )
                }
            }

            is AuthEvents.OnSignedInWithGoogle -> onSignedWithGoogle(
                firebaseUser = event.firebaseUser
            )
        }
    }

    private fun onSignedWithGoogle(firebaseUser: FirebaseUser?) {
        if (firebaseUser == null) {
            viewModelScope.launch {
                SnackbarController.sendAlert("Unable to login with Google")
            }
            return
        }

        signInJob?.cancel()
        signUpJob?.cancel()
        signInJob = CoroutineScope(Dispatchers.IO).launch {
            _state.update {
                it.copy(
                    isLoading = true
                )
            }
            val isAlreadyExist = try {
                repository.checkIfUserAlreadyExistInDatabase()
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
                SnackbarController.sendAlert(e.message)
                return@launch
            }
            if (isAlreadyExist) {
                /* Todo Navigate to main app screen here
                    navigator.navigateTo(
                    MainScreens.Root,
                    popUpTo = AuthScreens.Root,
                    inclusive = true
                )*/
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
                return@launch
            }

            val userData = UserData(
                name = firebaseUser.displayName,
                email = firebaseUser.email!!,
                userId = firebaseUser.uid,
                profilePhoto = firebaseUser.photoURL,
                method = SignInMethod.GOOGLE
            )

            try {
                // todo also check if exist then ignore
                repository.createUserInDatabase(
                    userData = userData
                )
                /* Todo Navigate to main app screen here
                    navigator.navigateTo(
                    MainScreen.Root,
                    popUpTo = AuthScreens.Root,
                    inclusive = true
                )*/
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false
                    )
                }
                SnackbarController.sendAlert(e.message)
            }
        }
    }


    private fun signUp() {
        _state.update {
            it.copy(
                user =
                    UserData(
                        method = SignInMethod.EMAIL,
                    ),
            )
        }
        signUpJob?.cancel()
        signUpJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.signUp(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password,
                ).collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(value.message)
                                it.copy(
                                    isLoading = false,

                                    )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success ->
                            _state.update {
                                /* Todo Navigate to main app screen here
                                    navigator.navigateTo(
                                    route = MainScreen.Root,
                                    popUpTo = AuthScreens.Root,
                                    inclusive = true,
                                )*/
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = false,
                                )
                            }
                    }
                }
            }
    }

    private fun signOut() {
        _state.update {
            it.copy(
                user = UserData(),
            )
        }
        signOutJob?.cancel()
        signOutJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.signOut().collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = false,
                                )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success -> {
                            val method = value.data
                            _state.update {
                                SnackbarController.sendAlert("Signed out successfully")
                                it.copy(
                                    isLoading = false,
                                    signOutWithGoogle = method == SignInMethod.GOOGLE
                                )
                            }

                            /* Todo Navigate to main app or onboarding screen here
                                navigator.navigateTo(
                                route = onBoardingScreen.Root,
                                popUpTo = AuthScreens.Root,
                                inclusive = true,
                            )*/
                            delay(1000L)
                            _state.update {
                                it.copy(
                                    isLoading = false,
                                    signOutWithGoogle = false
                                )
                            }
                        }
                    }
                }
            }
    }

    private fun signIn() {
        _state.update {
            it.copy(
                user =
                    UserData(
                        method = SignInMethod.EMAIL,
                    ),
            )
        }
        signInJob?.cancel()
        signInJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.signIn(
                    email = _state.value.email, password = _state.value.password,
                ).collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(value.message)

                                it.copy(
                                    isLoading = false,
                                )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success ->
                            _state.update {
                                /* Todo Navigate to main app screen here
                                    navigator.navigateTo(
                                    route = GeneratorScreens.Root,
                                    popUpTo = AuthScreens.Root,
                                    inclusive = true,
                                )*/
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = false,
                                )
                            }
                    }
                }
            }
    }

    private fun forgetPassword() {
        forgetPasswordJob?.cancel()
        forgetPasswordJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.forgetPassword(
                    email = _state.value.email,
                ).collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(value.message)
                                it.copy(
                                    isLoading = false,
                                )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success ->
                            _state.update {
                                SnackbarController.sendAlert("Please check your email")
                                it.copy(
                                    isLoading = false,
                                )
                            }
                    }
                }
            }
    }

    private fun deleteAccount() {
        deleteAccountJob?.cancel()
        deleteAccountJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.deleteAccount(
                    password = _state.value.deletePassword,
                ).collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(value.message)
                                it.copy(
                                    isLoading = false,
                                )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success ->
                            _state.update {
                                // todo navigate to main screen
                                /*  navigator.navigateTo(
                                      route = AuthScreens.Root,
                                      popUpTo = AppMakerScreens.Root,
                                      inclusive = true,
                                  )*/
                                SnackbarController.sendAlert("Account deleted successfully")
                                it.copy(
                                    isLoading = false,
                                    deletePassword = "",
                                    deletePasswordError = "",
                                    password = "",
                                    passwordError = "",
                                    confirmPassword = "",
                                    confirmPasswordError = "",
                                    currentPassword = "",
                                    currentPasswordError = "",
                                )
                            }
                    }
                }
            }
    }

    private fun changePassword() {
        changePasswordJob?.cancel()
        changePasswordJob =
            CoroutineScope(Dispatchers.IO).launch {
                repository.changePassword(
                    oldPassword = _state.value.currentPassword,
                    newPassword = _state.value.newPassword,
                ).collect { value ->
                    when (value) {
                        is RequestState.Error ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = false,
                                )
                            }

                        RequestState.Idle -> Unit
                        RequestState.Loading ->
                            _state.update {
                                SnackbarController.sendAlert(null)
                                it.copy(
                                    isLoading = true,
                                )
                            }

                        is RequestState.Success ->
                            _state.update {
                                // todo navigate to main screen
                                /* navigator.navigateTo(
                                     route = AppMakerScreens.Root,
                                     popUpTo = AppMakerScreens.Root,
                                     inclusive = true,
                                 )*/
                                SnackbarController.sendAlert(
                                    if (value.data) "Password changed" else "Unable to change password",
                                )
                                it.copy(
                                    isLoading = false,
                                )
                            }
                    }
                }
            }
    }

    // functions for textFields
    fun onEmailChange(value: String) {
        val error = if (validateEmail(value)) "" else "Please enter valid email"
        _state.update {
            it.copy(
                email = value.trim(),
                emailError = error,
            )
        }
    }

    private fun validateEmail(email: String) =
        if (email.isEmpty()) {
            false
        } else {
            val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
            email.matches(emailPattern.toRegex())
        }

    fun onPasswordChange(value: String) {
        val confirmPassword = _state.value.confirmPassword
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                confirmPassword.isNotEmpty() && confirmPassword != value -> "Password doesn't match"
                else -> ""
            }
        _state.update {
            it.copy(
                password = value.trim(),
                passwordError = error,
            )
        }
    }

    fun onDeletePasswordChange(value: String) {
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                else -> ""
            }
        _state.update {
            it.copy(
                deletePassword = value.trim(),
                deletePasswordError = error,
            )
        }
    }

    fun onConfirmPasswordChange(value: String) {
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                value != _state.value.password -> "Password doesn't match"
                else -> ""
            }
        _state.update {
            it.copy(
                confirmPassword = value.trim(),
                confirmPasswordError = error,
            )
        }
    }

    fun onCurrentPasswordChange(value: String) {
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                else -> ""
            }
        _state.update {
            it.copy(
                currentPassword = value.trim(),
                currentPasswordError = error,
            )
        }
    }

    fun onNewPasswordChange(value: String) {
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                value == _state.value.currentPassword -> "Same as current password"
                else -> ""
            }
        _state.update {
            it.copy(
                newPassword = value.trim(),
                newPasswordError = error,
            )
        }
    }

    fun onConfirmNewPasswordChange(value: String) {
        val error =
            when {
                value.length < MIN_PASSWORD_LENGTH -> "Minimum length should be $MIN_PASSWORD_LENGTH"
                value == _state.value.currentPassword -> "Same as current password"
                value != _state.value.newPassword -> "Password doesn't match"
                else -> ""
            }
        _state.update {
            it.copy(
                confirmPassword = value.trim(),
                confirmPasswordError = error,
            )
        }
    }

    fun onNameChange(value: String) {
        val error =
            if (value.length >= MIN_NAME_LENGTH) "" else "Minimum length should be $MIN_NAME_LENGTH"
        _state.update {
            it.copy(
                name = value,
                nameError = error,
            )
        }
    }
}
