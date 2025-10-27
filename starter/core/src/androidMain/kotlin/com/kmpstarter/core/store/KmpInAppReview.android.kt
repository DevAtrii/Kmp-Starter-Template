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

package com.kmpstarter.core.store

import android.app.Activity
import com.google.android.play.core.review.ReviewManager
import kotlinx.coroutines.tasks.await

actual class KmpInAppReview(
    private val activity: Activity,
    private val manager: ReviewManager,
) {
    @Throws(exceptionClasses = [Exception::class])
    actual suspend fun askForReview() {
        val request = manager.requestReviewFlow()
        val reviewInfo = request.await()

        manager.launchReviewFlow(activity, reviewInfo).await()
    }
}