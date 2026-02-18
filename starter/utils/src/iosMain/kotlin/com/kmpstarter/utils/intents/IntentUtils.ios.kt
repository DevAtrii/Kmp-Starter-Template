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

@file:OptIn(ExperimentalForeignApi::class)

package com.kmpstarter.utils.intents


import interop.SwiftIntentUtils
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.MessageUI.MFMailComposeResult
import platform.MessageUI.MFMailComposeViewController
import platform.MessageUI.MFMailComposeViewControllerDelegateProtocol
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationOpenSettingsURLString
import platform.UIKit.UIPasteboard
import platform.darwin.NSObject


class MailDelegate : NSObject(), MFMailComposeViewControllerDelegateProtocol {
    override fun mailComposeController(
        controller: MFMailComposeViewController,
        didFinishWithResult: MFMailComposeResult,
        error: NSError?,
    ) {
        controller.dismissViewControllerAnimated(true, null)
    }
}


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class IntentUtils {
    actual fun openUrl(url: String): Boolean {
        SwiftIntentUtils.openUrlWithStr(str = url)
        return true
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

    actual fun copyToClipboard(text: String) {
        UIPasteboard.generalPasteboard.string = text
    }

    actual fun shareText(text: String) {
        val activityViewController = UIActivityViewController(
            activityItems = listOf(text),
            applicationActivities = null
        )
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(
            viewControllerToPresent = activityViewController,
            animated = true,
            completion = null
        )
    }

    actual fun getClipboardText() = UIPasteboard.generalPasteboard.string

    actual fun sendEmail(email: String, subject: String, body: String) {
        if (!MFMailComposeViewController.canSendMail()) {
            println("Mail not configured on device")
            return
        }

        val mail = MFMailComposeViewController().apply {
            setToRecipients(toRecipients = listOf(email))
            setSubject(subject = subject)
            setMessageBody(body = body, false)
            mailComposeDelegate = MailDelegate()
        }

        val rootVC = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootVC?.presentViewController(mail, animated = true, completion = null)
    }

}