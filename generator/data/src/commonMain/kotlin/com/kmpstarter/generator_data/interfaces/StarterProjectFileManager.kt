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

package com.kmpstarter.generator_data.interfaces


typealias Path = String
typealias FilePath = String
typealias FolderPath = String

interface StarterProjectFileManager {

    companion object {
        suspend fun StarterProjectFileManager.getFileAs(path: FilePath): Result<String> =
            runCatching {
                val fileBytes = getFile(path = path).getOrThrow()

                fileBytes.decodeToString()
            }
    }

    suspend fun writeFile(path: FilePath, content: ByteArray):  Result<Unit>
    suspend fun mkDirs(path: FolderPath):  Result<Unit>

    suspend fun getFiles(path: FolderPath): List<FilePath>
    suspend fun getFile(path: FilePath): Result<ByteArray>

    suspend fun delete(path: Path): Result<Unit>

    suspend fun extractZip(path: FilePath, output: FolderPath):  Result<Unit>

}



















