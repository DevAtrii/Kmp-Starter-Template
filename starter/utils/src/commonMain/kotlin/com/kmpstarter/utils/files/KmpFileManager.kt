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

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class KmpFileManager {
    companion object {}

    suspend fun saveFileToDownloadsFolder(
        fileName: String,
        folderName: String,
        fileContent: ByteArray,
        mimeType: String,
    ): Result<Unit>

    suspend fun saveFileIn(fileName: String, fileContent: ByteArray): Result<Unit>

    suspend fun readFileFromCache(fileName: String): Result<ByteArray>
    suspend fun saveFileInCache(fileName: String, fileContent: ByteArray): Result<Unit>

}