/*
 *
 *  * Copyright (c) 2025
 *  *
 *  * Author: Athar Gul
 *  * GitHub: https://github.com/DevAtrii
 *  * YouTube: https://www.youtube.com/@devatrii/videos
 *  *
 *  * All rights reserved.
 *
 *
 */

package com.kmpstarter.utils.files

val KmpFileManager.Companion.TAG: String
    get() = "KmpFileManager"

/**
 * Legacy cross-platform file manager.
 *
 * **Deprecated:** Use [StarterFileManager] instead. It provides a richer API with typed
 * file metadata ([StarterFile]), separate file name/extension handling, MIME type support,
 * and directory listing for Downloads and cache.
 *
 * ### Migration examples
 *
 * **Save to Downloads**
 * ```kotlin
 * // Old
 * kmpFileManager.saveFileToDownloadsFolder(
 *     fileName = "report.pdf",
 *     folderName = "MyApp",
 *     fileContent = bytes,
 *     mimeType = "application/pdf",
 * )
 *
 * // New
 * starterFileManager.saveFileIntoDownloads(
 *     file = "report",
 *     folderPath = "MyApp",
 *     extension = "pdf",
 *     content = bytes,
 *     mimeType = "application/pdf",
 * )
 * ```
 *
 * **Save with system picker**
 * ```kotlin
 * // Old
 * kmpFileManager.saveFileIn(
 *     fileName = "export.csv",
 *     fileContent = bytes,
 * )
 *
 * // New
 * starterFileManager.saveFileIn(
 *     suggestedName = "export",
 *     extension = "csv",
 *     content = bytes,
 *     mimeType = "text/csv",
 * )
 * ```
 *
 * **Cache read/write**
 * ```kotlin
 * // Old
 * kmpFileManager.saveFileInCache(fileName = "cache.bin", fileContent = bytes)
 * kmpFileManager.readFileFromCache(fileName = "cache.bin")
 *
 * // New
 * starterFileManager.saveInCache(
 *     file = "cache",
 *     folderPath = null,
 *     extension = "bin",
 *     content = bytes,
 *     mimeType = "application/octet-stream",
 * )
 * starterFileManager.readFromCache(path = "cache.bin")
 * ```
 */
@Deprecated(
    message = "Use StarterFileManager instead. See class KDoc for migration examples.",
    replaceWith = ReplaceWith("StarterFileManager"),
)
expect class KmpFileManager {
    companion object {}

    /**
     * Saves a file into the Downloads folder.
     *
     * @deprecated Use [StarterFileManager.saveFileIntoDownloads].
     */
    @Deprecated(
        message = "Use StarterFileManager.saveFileIntoDownloads instead.",
        replaceWith = ReplaceWith(
            expression = "starterFileManager.saveFileIntoDownloads(file = fileName.substringBeforeLast('.'), folderPath = folderName, extension = fileName.substringAfterLast('.'), content = fileContent, mimeType = mimeType)",
            imports = ["com.kmpstarter.utils.files.StarterFileManager"],
        ),
    )
    suspend fun saveFileToDownloadsFolder(
        fileName: String,
        folderName: String,
        fileContent: ByteArray,
        mimeType: String,
    ): Result<Unit>

    /**
     * Prompts the user to choose where to save a file.
     *
     * @deprecated Use [StarterFileManager.saveFileIn].
     */
    @Deprecated(
        message = "Use StarterFileManager.saveFileIn instead.",
        replaceWith = ReplaceWith(
            expression = "starterFileManager.saveFileIn(suggestedName = fileName.substringBeforeLast('.'), extension = fileName.substringAfterLast('.'), content = fileContent, mimeType = \"application/octet-stream\")",
            imports = ["com.kmpstarter.utils.files.StarterFileManager"],
        ),
    )
    suspend fun saveFileIn(fileName: String, fileContent: ByteArray): Result<Unit>

    /**
     * Reads file contents from cache.
     *
     * @deprecated Use [StarterFileManager.readFromCache].
     */
    @Deprecated(
        message = "Use StarterFileManager.readFromCache instead.",
        replaceWith = ReplaceWith(
            expression = "starterFileManager.readFromCache(path = fileName)",
            imports = ["com.kmpstarter.utils.files.StarterFileManager"],
        ),
    )
    suspend fun readFileFromCache(fileName: String): Result<ByteArray>

    /**
     * Saves file into cache.
     *
     * @deprecated Use [StarterFileManager.saveInCache].
     */
    @Deprecated(
        message = "Use StarterFileManager.saveInCache instead.",
        replaceWith = ReplaceWith(
            expression = "starterFileManager.saveInCache(file = fileName.substringBeforeLast('.'), folderPath = null, extension = fileName.substringAfterLast('.'), content = fileContent, mimeType = \"application/octet-stream\")",
            imports = ["com.kmpstarter.utils.files.StarterFileManager"],
        ),
    )
    suspend fun saveFileInCache(fileName: String, fileContent: ByteArray): Result<Unit>
}
