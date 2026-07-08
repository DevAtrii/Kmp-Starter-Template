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

expect class FileManagerImpl() : StarterProjectFileManager {
    override fun getCurrentDir(): FolderPath
    override suspend fun writeFile(
        path: FilePath,
        content: ByteArray,
    ): Result<Unit>

    override suspend fun getFilesRecursively(path: FolderPath): List<FilePath>
    override suspend fun mkDirs(path: FolderPath): Result<Unit>
    override suspend fun getDirectoriesRecursively(path: FolderPath): List<FolderPath>
    override suspend fun moveFiles(
        path: FolderPath,
        to: FolderPath,
    ): Result<Unit>

    override suspend fun getFiles(path: FolderPath): List<FilePath>
    override suspend fun getFile(path: FilePath): Result<ByteArray>
    override suspend fun delete(path: Path): Result<Unit>
    override suspend fun extractZip(
        path: FilePath,
        output: FolderPath,
    ): Result<Unit>

    override suspend fun createZip(path: Path): Result<ByteArray>
    override suspend fun rename(
        path: Path,
        to: String,
    ): Result<Unit>
}