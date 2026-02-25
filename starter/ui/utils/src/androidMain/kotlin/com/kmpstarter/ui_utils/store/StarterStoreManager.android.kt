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
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.retain.retain
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import kotlinx.coroutines.tasks.await

actual class StarterStoreManager(
    private val activity: Activity,
) {

    private val reviewManager by lazy {
        ReviewManagerFactory.create(
            activity.applicationContext
        )
    }

    private val updateManager by lazy {
        AppUpdateManagerFactory.create(activity.applicationContext)
    }

    companion object {
        private const val UPDATE_REQUEST_CODE = 1001
    }

    @Throws(exceptionClasses = [Exception::class])
    actual suspend fun askForReview() {
        val request = reviewManager.requestReviewFlow()
        val reviewInfo = request.await()

        reviewManager.launchReviewFlow(activity, reviewInfo).await()
    }

    @Suppress("UNCHECKED_CAST")
    actual suspend fun checkAppUpdate(
        launcher: UpdateLauncher,
        force: Boolean,
        onUpdateUnAvailable: () -> Unit,
        onUpdateAvailable: () -> Unit,
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ) {
        val appUpdateInfoTask = updateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            val updateType = if (force) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE

            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                && appUpdateInfo.isUpdateTypeAllowed(updateType)
            ) {
                onUpdateAvailable()
                val platformLauncher = launcher.provide(
                    onUpdated = onUpdated,
                    onUpdateFailure = onUpdateFailure
                ) as ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>

                updateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    platformLauncher,
                    AppUpdateOptions.newBuilder(updateType).build()
                )
            } else {
                onUpdateUnAvailable()
            }
        }.addOnFailureListener {
            onUpdateFailure()
        }
    }


}


@SuppressLint("ContextCastToActivity")
@Composable
actual fun rememberStarterStoreManager(): StarterStoreManager {
    val activity = LocalActivity.current ?: LocalContext.current as Activity
    return retain {
        StarterStoreManager(
            activity = activity,
        )
    }
}





















