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

package com.kmpstarter.utils.files


import com.kmpstarter.utils.logging.Log
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.toByteString
import platform.Foundation.NSData
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.NSUserDomainMask
import platform.Foundation.dataWithBytes
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.writeToFile
import platform.Foundation.writeToURL
import platform.UIKit.UIApplication
import platform.UIKit.UIDocumentPickerViewController
import platform.UIKit.UIModalPresentationFullScreen


actual class KmpFileManager {
    actual suspend fun saveFileToDownloadsFolder(
        fileName: String,
        folderName: String,
        fileContent: ByteArray,
        mimeType: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // todo fix this
            // Get the Documents directory
            val paths = NSSearchPathForDirectoriesInDomains(
                directory = NSDocumentDirectory,
                domainMask = NSUserDomainMask,
                expandTilde = true
            )
            val documentsPath = paths.firstOrNull() as? String
                ?: return@withContext Result.failure(Exception("Unable to find Documents directory"))

            // Create the file URL
            val filePath = "$documentsPath/$folderName/$fileName"
            val fileUrl = NSURL.fileURLWithPath(filePath)
            Log.d("KmpFileManager", "FilePath: $filePath, File URL: $fileUrl")

            // Write the file
            val nsData = fileContent.toNSData()
            val success = nsData.writeToURL(fileUrl, atomically = true)

            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to write file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveFileIn(fileName: String, fileContent: ByteArray): Result<Unit> =
        withContext(Dispatchers.IO) {
            val tempDir = NSTemporaryDirectory()
            val filePath = "$tempDir/$fileName"
            fileContent.toNSData().writeToFile(filePath, atomically = true)
            val fileUrl = NSURL.fileURLWithPath(filePath)

            val picker = UIDocumentPickerViewController(forExportingURLs = listOf(fileUrl))
            picker.modalPresentationStyle = UIModalPresentationFullScreen

            withContext(Dispatchers.Main) {
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                    picker,
                    animated = true,
                    completion = null
                )
            }
            return@withContext Result.success(Unit)
        }


    // need testing
    actual suspend fun readFileFromCache(
        fileName: String,
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val filePath = "${NSTemporaryDirectory()}/$fileName"
            val data = NSData.dataWithContentsOfFile(filePath)
                ?: return@withContext Result.failure(
                    Exception("File not found")
                )
            val byteArr = data.toByteString().toByteArray()
            Result.success(byteArr)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveFileInCache(
        fileName: String,
        fileContent: ByteArray,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val filePath = "${NSTemporaryDirectory()}/$fileName"

            val success = fileContent
                .toNSData()
                .writeToFile(filePath, atomically = true)

            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Failed to save cache file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual companion object

}

// Helper extension to convert ByteArray → NSData
@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.dataWithBytes(
            bytes = pinned.addressOf(0),
            length = this.size.toULong()
        )
    }
}