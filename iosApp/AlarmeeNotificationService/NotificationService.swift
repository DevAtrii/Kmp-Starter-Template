//
//  NotificationService.swift
//  AlarmeeNotificationService
//
//  Created by Vivien Mahé on 06/07/2025.
//

import UserNotifications

class NotificationService: UNNotificationServiceExtension {

    var contentHandler: ((UNNotificationContent) -> Void)?
    var bestAttemptContent: UNMutableNotificationContent?

    override func didReceive(_ request: UNNotificationRequest, withContentHandler contentHandler: @escaping (UNNotificationContent) -> Void) {
        self.contentHandler = contentHandler
        bestAttemptContent = (request.content.mutableCopy() as? UNMutableNotificationContent)

        guard let bestAttemptContent = bestAttemptContent,
              let imageUrlString = bestAttemptContent.userInfo["imageUrl"] as? String,
              let url = URL(string: imageUrlString) else {
            contentHandler(request.content)
            return
        }

        downloadImage(from: url) { attachment in
            if let attachment = attachment {
                bestAttemptContent.attachments = [attachment]
            }
            contentHandler(bestAttemptContent)
        }
    }

    override func serviceExtensionTimeWillExpire() {
        // Called just before the extension will be terminated by the system.
        // Use this as an opportunity to deliver your "best attempt" at modified content, otherwise the original push payload will be used.
        if let contentHandler = contentHandler, let bestAttemptContent =  bestAttemptContent {
            contentHandler(bestAttemptContent)
        }
    }

    private func downloadImage(from url: URL, completion: @escaping (UNNotificationAttachment?) -> Void) {
        let task = URLSession.shared.downloadTask(with: url) { downloadUrl, _, _ in
            guard let downloadUrl = downloadUrl else {
                completion(nil)
                return
            }

            let tmpDirectory = FileManager.default.temporaryDirectory
            let tmpFile = tmpDirectory.appendingPathComponent(UUID().uuidString + ".jpg")

            do {
                try FileManager.default.moveItem(at: downloadUrl, to: tmpFile)
                let attachment = try UNNotificationAttachment(identifier: "image", url: tmpFile, options: nil)
                completion(attachment)
            } catch {
                completion(nil)
            }
        }

        task.resume()
    }
}
