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

import com.kmpstarter.utils.starter.ExperimentalStarterApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import okio.ByteString.Companion.toByteString
import platform.Foundation.NSData
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
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

@OptIn(ExperimentalStarterApi::class, ExperimentalForeignApi::class)
actual class StarterFileManager {
    actual companion object {}

    actual suspend fun saveFileIn(
        suggestedName: FileName,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileName = buildFileName(suggestedName, extension)
            val tempDir = NSTemporaryDirectory()
            val filePath = "$tempDir/$fileName"
            val fileUrl = NSURL.fileURLWithPath(filePath)

            val success = content.toNSData().writeToFile(filePath, atomically = true)
            if (!success) {
                return@withContext Result.failure(IllegalStateException("Failed to write temporary file"))
            }

            val picker = UIDocumentPickerViewController(forExportingURLs = listOf(fileUrl))
            picker.modalPresentationStyle = UIModalPresentationFullScreen

            withContext(Dispatchers.Main) {
                UIApplication.sharedApplication.keyWindow?.rootViewController?.presentViewController(
                    picker,
                    animated = true,
                    completion = null,
                )
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveFileIntoDownloads(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val directoryPath = resolveDocumentsDirectoryPath(folderPath)
                ?: return@withContext Result.failure(IllegalStateException("Unable to find Documents directory"))

            val filePath = "$directoryPath/${buildFileName(file, extension)}"
            val fileUrl = NSURL.fileURLWithPath(filePath)
            val success = content.toNSData().writeToURL(fileUrl, atomically = true)

            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to write file to Documents"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun getFilesFromDownloads(
        path: FolderPath?,
    ): Result<List<StarterFile>> = withContext(Dispatchers.IO) {
        try {
            val directoryPath = resolveDocumentsDirectoryPath(path)
                ?: return@withContext Result.failure(IllegalStateException("Unable to find Documents directory"))

            Result.success(listFilesInDirectory(directoryPath))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun readFromDownloads(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>> = withContext(Dispatchers.IO) {
        try {
            if (path.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Downloads file path is required"))
            }

            val data = NSData.dataWithContentsOfFile(path)
                ?: return@withContext Result.failure(IllegalStateException("Downloads file not found: $path"))

            val starterFile = buildStarterFileAtPath(path)
                ?: return@withContext Result.failure(IllegalStateException("Unable to read Downloads file metadata: $path"))

            Result.success(starterFile to data.toByteString().toByteArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun getFilesFromCache(
        path: FolderPath,
    ): Result<List<StarterFile>> = withContext(Dispatchers.IO) {
        try {
            val directoryPath = resolveCacheDirectoryPath(path)
                ?: return@withContext Result.failure(IllegalStateException("Unable to find cache directory"))

            Result.success(listFilesInDirectory(directoryPath))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun readFromCache(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>> = withContext(Dispatchers.IO) {
        try {
            val cacheRoot = resolveCacheDirectoryPath("")
                ?: return@withContext Result.failure(IllegalStateException("Unable to find cache directory"))

            val filePath = "$cacheRoot/$path"
            val data = NSData.dataWithContentsOfFile(filePath)
                ?: return@withContext Result.failure(IllegalStateException("Cache file not found: $path"))

            val starterFile = buildStarterFileAtPath(filePath)
                ?: return@withContext Result.failure(IllegalStateException("Unable to read cache file metadata: $path"))

            Result.success(starterFile to data.toByteString().toByteArray())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveInCache(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val directoryPath = resolveCacheDirectoryPath(folderPath.orEmpty())
                ?: return@withContext Result.failure(IllegalStateException("Unable to find cache directory"))

            val filePath = "$directoryPath/${buildFileName(file, extension)}"
            val success = content.toNSData().writeToFile(filePath, atomically = true)

            if (success) {
                Result.success(Unit)
            } else {
                Result.failure(IllegalStateException("Failed to save cache file"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun resolveDocumentsDirectoryPath(folderPath: FolderPath?): String? {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSDocumentDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        )
        val documentsPath = paths.firstOrNull() as? String ?: return null
        val targetPath = folderPath?.let { "$documentsPath/$it" } ?: documentsPath

        NSFileManager.defaultManager.createDirectoryAtPath(
            path = targetPath,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )

        return targetPath
    }

    private fun resolveCacheDirectoryPath(folderPath: FolderPath): String? {
        val paths = NSSearchPathForDirectoriesInDomains(
            directory = NSCachesDirectory,
            domainMask = NSUserDomainMask,
            expandTilde = true,
        )
        val cachePath = paths.firstOrNull() as? String ?: return null
        val targetPath = if (folderPath.isBlank()) cachePath else "$cachePath/$folderPath"

        NSFileManager.defaultManager.createDirectoryAtPath(
            path = targetPath,
            withIntermediateDirectories = true,
            attributes = null,
            error = null,
        )

        return targetPath
    }

    private fun buildStarterFileAtPath(filePath: String): StarterFile? {
        val fileManager = NSFileManager.defaultManager
        if (!fileManager.fileExistsAtPath(filePath)) return null

        val attributes = fileManager.attributesOfItemAtPath(filePath, error = null) ?: return null
        if (attributes["NSFileType"] as? String == "NSFileTypeDirectory") return null

        val fileName = filePath.substringAfterLast('/')
        val (name, extension) = splitFileName(fileName)
        return StarterFile(
            path = filePath,
            name = name,
            extension = extension,
            mimeType = null,
            sizeBytes = attributes["NSFileSize"] as? Long,
            createdAtMillis = null,
            modifiedAtMillis = null,
        )
    }

    private fun listFilesInDirectory(directoryPath: String): List<StarterFile> {
        val fileManager = NSFileManager.defaultManager
        val contents = fileManager.contentsOfDirectoryAtPath(directoryPath, error = null) ?: return emptyList()

        return contents
            .filterIsInstance<String>()
            .mapNotNull { fileName ->
                val filePath = "$directoryPath/$fileName"
                if (!fileManager.fileExistsAtPath(filePath)) return@mapNotNull null

                val attributes = fileManager.attributesOfItemAtPath(filePath, error = null) ?: return@mapNotNull null
                if (attributes["NSFileType"] as? String == "NSFileTypeDirectory") return@mapNotNull null

                val (name, extension) = splitFileName(fileName)
                StarterFile(
                    path = filePath,
                    name = name,
                    extension = extension,
                    mimeType = null,
                    sizeBytes = attributes["NSFileSize"] as? Long,
                    createdAtMillis = null,
                    modifiedAtMillis = null,
                )
            }
    }

    private fun buildFileName(file: FileName, extension: FileExtension): String {
        return if (extension.isBlank()) file else "$file.$extension"
    }

    private fun splitFileName(fileName: String): Pair<String, String> {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex <= 0 || dotIndex == fileName.length - 1) {
            fileName to ""
        } else {
            fileName.substring(0, dotIndex) to fileName.substring(dotIndex + 1)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun ByteArray.toNSData(): NSData {
    return this.usePinned { pinned ->
        NSData.dataWithBytes(
            bytes = pinned.addressOf(0),
            length = this.size.toULong(),
        )
    }
}
