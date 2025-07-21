package com.kmpstarter.core.utils.intents

import com.kmpstarter.core.APPSTORE_URL
import com.kmpstarter.core.utils.logging.Log
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
import kotlin.collections.listOf


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