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

package com.kmpstarter.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.kmpstarter.utils.StarterAndroidProvider.activity
import com.kmpstarter.utils.StarterAndroidProvider.application
import com.kmpstarter.utils.StarterAndroidProvider.onActivityCreated
import com.kmpstarter.utils.StarterAndroidProvider.onActivityResumed
import com.kmpstarter.utils.StarterAndroidProvider.onActivityStarted

/**
 * Holds the Android [Application] and the current [Activity] for `starter/utils`.
 *
 * Set [application] **once** via [StarterApplicationProvider] (`androidx.startup`).
 * [activity] tracks the foreground screen through [Application.ActivityLifecycleCallbacks].
 *
 * Use [requireApplication] / [requireActivity] when an Android API needs a non-null
 * [Context][android.content.Context] or [Activity] (purchases, [com.kmpstarter.utils.intents.IntentUtils],
 * file pickers).
 *
 * ### Example
 * ```kotlin
 * val app = StarterAndroidProvider.requireApplication()
 * val activity = StarterAndroidProvider.requireActivity()
 * ```
 *
 * Do not assign [application] from `Application.onCreate` unless you removed
 * [StarterApplicationProvider] from the manifest.
 */
@SuppressLint("StaticFieldLeak") // Not leaking — [activity] is cleared on stop/destroy.
object StarterAndroidProvider : Application.ActivityLifecycleCallbacks {
    /**
     * Process [Application]. Set once by [StarterApplicationProvider]; later writes throw.
     */
    var application: Application? = null
        set(value) {
            require(value != null) { "`application` should not be set to a null value." }
            require(field == null) { "`application` is already set." }
            value.registerActivityLifecycleCallbacks(this)
            field = value
        }

    /**
     * Foreground [Activity], or `null` when none is started.
     *
     * Updated on create / start / resume. Cleared on stop / destroy of the same instance.
     * Left intact on pause so rapid Activity-based calls (purchases, intents) still have a host.
     */
    var activity: Activity? = null
        private set

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        StarterAndroidProvider.activity = activity
    }

    override fun onActivityStarted(activity: Activity) {
        StarterAndroidProvider.activity = activity
    }

    override fun onActivityResumed(activity: Activity) {
        StarterAndroidProvider.activity = activity
    }

    /**
     * Intentionally a no-op.
     *
     * Clearing [activity] here would null the host between pause and the next create/resume.
     * Back-to-back Activity APIs (e.g. RevenueCat purchase, share sheet) would then crash.
     * The next [onActivityCreated] / [onActivityStarted] / [onActivityResumed] replaces the ref.
     */
    override fun onActivityPaused(activity: Activity) {
        // no-op
    }

    override fun onActivityStopped(activity: Activity) {
        if (activity == StarterAndroidProvider.activity) StarterAndroidProvider.activity = null
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        // no-op
    }

    override fun onActivityDestroyed(activity: Activity) {
        if (activity == StarterAndroidProvider.activity) StarterAndroidProvider.activity = null
    }
}

/**
 * Non-null [Application], or an error that explains how to keep `androidx.startup` in the manifest.
 *
 * @throws IllegalStateException if [StarterApplicationProvider] never ran
 */
fun StarterAndroidProvider.requireApplication(): Application =
    application ?: error(
        "KMP-Starter-Template has no reference to the Application. Please make sure you have not removed " +
                "the androidx.startup.InitializationProvider from your AndroidManifest.xml. If you " +
                "need to remove specific initializers, such as " +
                "androidx.work.WorkManagerInitializer, do so as follows:" +
                "\n\n" +
                $$"""
        <provider
            android:name="androidx.startup.InitializationProvider"
            android:exported="false"
            android:authorities="${applicationId}.androidx-startup"
            tools:node="merge">
        
            <meta-data
                android:name="androidx.work.WorkManagerInitializer"
                android:value="androidx.startup"
                tools:node="remove" />
        
        </provider>
        """.trimIndent() +
                "\n\n" +
                "Stack trace:"
    )

/**
 * Non-null foreground [Activity].
 *
 * @throws IllegalStateException if no Activity is started (app backgrounded, or before first screen)
 */
fun StarterAndroidProvider.requireActivity(): Activity =
    activity ?: error("There's no current Activity.")

