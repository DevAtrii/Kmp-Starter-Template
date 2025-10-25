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

package com.kmpstarter.utils.intents

/**
 * Utility functions for handling links and URLs across platforms.
 * Provides platform-specific implementations for common link operations.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class IntentUtils {
    /**
     * Opens a URL in the default browser or appropriate app.
     *
     * @param url The URL to open
     * @return true if the URL was successfully opened, false otherwise
     */
    fun openUrl(url: String): Boolean


    /**
     * Opens the accessibility page for the current platform.
     *
     * @return true if the store page was successfully opened, false otherwise
     */
    fun openAccessibility(): Boolean

    fun copyToClipboard(text: String)
    fun shareText(text: String)

    fun getClipboardText(): String?

    fun sendEmail(email: String, subject: String, body: String)
}