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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.review.ReviewManagerFactory
import com.kmpstarter.utils.logging.Log
import kotlinx.coroutines.CancellationException
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

    @Throws(exceptionClasses = [Exception::class])
    actual suspend fun askForReview() {
        val request = reviewManager.requestReviewFlow()
        val reviewInfo = request.await()

        reviewManager.launchReviewFlow(activity, reviewInfo).await()
    }

    @Suppress("UNUSED_PARAMETER")
    actual suspend fun checkAppUpdate(
        launcher: UpdateLauncher,
        force: Boolean,
        onUpdateUnAvailable: () -> Unit,
        onUpdateAvailable: () -> Unit,
        onUpdated: () -> Unit,
        onUpdateFailure: () -> Unit,
    ) {
        if (!activity.canLaunchUpdateFlow()) {
            onUpdateFailure()
            return
        }

        try {
            val appUpdateInfo = updateManager.appUpdateInfo.await()
            val updateType = if (force) AppUpdateType.IMMEDIATE else AppUpdateType.FLEXIBLE
            val availability = appUpdateInfo.updateAvailability()
            val canStartUpdate = availability == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(updateType)
            val shouldResumeImmediate = force &&
                availability == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

            if (!canStartUpdate && !shouldResumeImmediate) {
                onUpdateUnAvailable()
                return
            }

            if (!activity.canLaunchUpdateFlow()) {
                onUpdateFailure()
                return
            }

            onUpdateAvailable()
            // Activity-based flow: Compose ActivityResultLauncher unregisters on
            // leave-composition, so Play's async callback used to call launch() on a
            // dead launcher → IllegalStateException.
            val resultCode = updateManager.startUpdateFlow(
                appUpdateInfo,
                activity,
                AppUpdateOptions.newBuilder(updateType).build(),
            ).await()

            if (resultCode == Activity.RESULT_OK) {
                onUpdated()
            } else {
                onUpdateFailure()
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.e(tag = null, "checkAppUpdate failed", e)
            onUpdateFailure()
        }
    }
}

private fun Activity.canLaunchUpdateFlow(): Boolean =
    !isFinishing && !isDestroyed

@SuppressLint("ContextCastToActivity")
@Composable
actual fun rememberStarterStoreManager(): StarterStoreManager {
    val activity = LocalActivity.current ?: LocalContext.current as Activity
    return remember(activity) {
        StarterStoreManager(
            activity = activity,
        )
    }
}
