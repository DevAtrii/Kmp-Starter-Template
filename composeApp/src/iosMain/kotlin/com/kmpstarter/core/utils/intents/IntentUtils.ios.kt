package com.kmpstarter.core.utils.intents

import com.kmpstarter.core.APPSTORE_URL
import com.kmpstarter.core.utils.logging.Log
import platform.Foundation.NSURL
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class IntentUtils {
    actual fun openUrl(url: String): Boolean {
        return try {
            val nsUrl = NSURL(string = url)
            val application = UIApplication.sharedApplication
            if (application.canOpenURL(nsUrl)) {
                application.openURL(nsUrl)
                true
            } else {
                Log.e(
                    tag = null,
                    "Cannot Open the Url"
                )
                false
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    actual fun openAppStore(): Boolean {
        return try {
            openUrl(
                url = APPSTORE_URL
            )
        } catch (e: Exception) {
            false
        }
    }

    actual fun openAccessibility(): Boolean {
        return try {
            val settingsUrl = NSURL(string = UIApplicationOpenSettingsURLString)
            val application = UIApplication.sharedApplication
            application.openURL(settingsUrl)
            true
        } catch (e: Exception) {
            false
        }
    }
}