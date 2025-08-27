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

package com.kmpstarter.starter_features.auth.presentation.ui_main.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kmpstarter.core.ui.components.buttons.GoogleSignInButtonUI
import com.mmk.kmpauth.firebase.google.GoogleButtonUiContainerFirebase
import dev.gitlive.firebase.auth.FirebaseUser

@Composable
fun GoogleSignInButton(
    modifier: Modifier = Modifier,
    setIsLoading: (Boolean) -> Unit = {},
    isLoading: Boolean = false,
    onFirebaseResult: (Result<FirebaseUser?>) -> Unit = {},
) {
    GoogleButtonUiContainerFirebase(
        modifier = modifier,
        onResult = {
            onFirebaseResult(it)
            setIsLoading(false)
        },
        linkAccount = false,
        filterByAuthorizedAccounts = true
    ) {
        GoogleSignInButtonUI(
            isGoogleSignInLoading = isLoading,
            onGoogleSignInClick = {
                this.onClick()
                setIsLoading(true)
            }
        )
    }

}