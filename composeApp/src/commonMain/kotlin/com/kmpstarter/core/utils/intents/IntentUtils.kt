package com.kmpstarter.core.utils.intents

/**
 * Utility functions for handling links and URLs across platforms.
 * Provides platform-specific implementations for common link operations.
 */ 
expect class IntentUtils {
    /**
     * Opens a URL in the default browser or appropriate app.
     *
     * @param url The URL to open
     * @return true if the URL was successfully opened, false otherwise
     */
    fun openUrl(url: String): Boolean

    /**
     * Opens the app store page for the current platform.
     *
     * @return true if the store page was successfully opened, false otherwise
     */
    fun openAppStore(): Boolean

    /**
     * Opens the accessibility page for the current platform.
     *
     * @return true if the store page was successfully opened, false otherwise
     */
    fun openAccessibility(): Boolean
}