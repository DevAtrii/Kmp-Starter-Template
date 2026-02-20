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

package com.kmpstarter.ui_utils.store

import android.annotation.SuppressLint
import android.app.Activity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember


private class AndroidUpdateLauncher(
    private val launcher: ManagedActivityResultLauncher<IntentSenderRequest, androidx.activity.result.ActivityResult>,
) : UpdateLauncher {
    var onUpdatedCallback: (() -> Unit)? = null
    var onUpdateFailureCallback: (() -> Unit)? = null

    override fun provide(
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ): PlatformLauncher {
        this.onUpdatedCallback = onUpdated
        this.onUpdateFailureCallback = onUpdateFailure

        return launcher
    }
}

private class UpdateLauncherHolder {
    var launcherInstance: AndroidUpdateLauncher? = null
}


@SuppressLint("ContextCastToActivity")
@Composable
actual fun rememberUpdateLauncher(): UpdateLauncher {
    val androidUpdateLauncher = remember {
        UpdateLauncherHolder()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            androidUpdateLauncher.launcherInstance?.onUpdatedCallback?.invoke()
        } else {
            androidUpdateLauncher.launcherInstance?.onUpdateFailureCallback?.invoke()
        }
    }

    return remember(launcher) {
        AndroidUpdateLauncher(launcher = launcher).also {
            androidUpdateLauncher.launcherInstance = it
        }
    }
}
