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

package com.kmpstarter.generator_data.impl

import com.kmpstarter.generator_data.interfaces.FilePath
import com.kmpstarter.generator_data.interfaces.FolderPath
import com.kmpstarter.generator_data.interfaces.Path
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager

actual class FileManagerImpl : StarterProjectFileManager {
    actual override fun getCurrentDir(): FolderPath {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun writeFile(
        path: FilePath,
        content: ByteArray,
    ): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun getFilesRecursively(path: FolderPath): List<FilePath> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun mkDirs(path: FolderPath): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun getDirectoriesRecursively(path: FolderPath): List<FolderPath> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun moveFiles(
        path: FolderPath,
        to: FolderPath,
    ): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun getFiles(path: FolderPath): List<FilePath> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun getFile(path: FilePath): Result<ByteArray> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun delete(path: Path): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun extractZip(
        path: FilePath,
        output: FolderPath,
    ): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun createZip(path: Path): Result<ByteArray> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }

    actual override suspend fun rename(
        path: Path,
        to: String,
    ): Result<Unit> {
        throw NotImplementedError("ONLY SUPPORTED ON JVM")
    }
}