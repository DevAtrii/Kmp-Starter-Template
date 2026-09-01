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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipOutputStream

private suspend inline fun <T> io(
    crossinline block: () -> T,
): T = withContext(Dispatchers.IO) {
    block()
}

actual class FileManagerImpl : StarterProjectFileManager {

    actual override fun getCurrentDir(): FolderPath =
        System.getProperty("user.dir") ?: "."

    actual override suspend fun writeFile(
        path: FilePath,
        content: ByteArray,
    ): Result<Unit> = io {
        runCatching {
            val file = File(path)
            file.parentFile?.mkdirs()
            file.writeBytes(content)
        }
    }

    actual override suspend fun getFilesRecursively(
        path: FolderPath,
    ): List<FilePath> = io {
        File(path).walkTopDown().filter { it.isFile }.map { it.path }.toList()
    }

    actual override suspend fun mkDirs(
        path: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            File(path).mkdirs()
            Unit
        }
    }

    actual override suspend fun getDirectoriesRecursively(
        path: FolderPath,
    ): List<FolderPath> = io {
        File(path).walkTopDown().filter { it.isDirectory }.map { it.path }.toList()
    }

    actual override suspend fun moveFiles(
        path: FolderPath,
        to: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            val source = File(path)
            val destination = File(to)
            destination.mkdirs()
            source.listFiles()?.forEach { child ->
                val target = File(destination, child.name)
                if (target.exists()) {
                    target.deleteRecursively()
                }
                check(child.renameTo(target)) { "Failed to move ${child.path} to ${target.path}" }
            }
            Unit
        }
    }

    actual override suspend fun getFiles(
        path: FolderPath,
    ): List<FilePath> = io {
        File(path).listFiles()?.filter { it.isFile }?.map { it.path }.orEmpty()
    }

    actual override suspend fun getFile(
        path: FilePath,
    ): Result<ByteArray> = io {
        runCatching { File(path).readBytes() }
    }

    actual override suspend fun delete(
        path: Path,
    ): Result<Unit> = io {
        runCatching {
            val target = File(path)
            if (!target.exists()) return@runCatching
            target.deleteRecursively()
            Unit
        }
    }

    actual override suspend fun extractZip(
        path: FilePath,
        output: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            val outputDir = File(output).canonicalFile
            outputDir.mkdirs()
            ZipFile(path).use { zip ->
                zip.entries().asSequence().forEach { entry ->
                    val target = File(outputDir, entry.name).canonicalFile
                    require(target.path.startsWith(outputDir.path)) {
                        "Invalid ZIP entry: ${entry.name}"
                    }
                    if (entry.isDirectory) {
                        target.mkdirs()
                    } else {
                        target.parentFile?.mkdirs()
                        zip.getInputStream(entry).use { input ->
                            target.outputStream().use { outputStream ->
                                input.copyTo(outputStream)
                            }
                        }
                    }
                }
            }
        }
    }

    actual override suspend fun createZip(
        path: Path,
    ): Result<ByteArray> = io {
        runCatching {
            val root = File(path)
            val output = ByteArrayOutputStream()
            ZipOutputStream(output).use { zip ->
                root.walkTopDown().filter { it.isFile }.forEach { file ->
                    val entryName = file.relativeTo(root).path.replace('\\', '/')
                    zip.putNextEntry(ZipEntry(entryName))
                    file.inputStream().use { it.copyTo(zip) }
                    zip.closeEntry()
                }
            }
            output.toByteArray()
        }
    }

    actual override suspend fun rename(
        path: Path,
        to: String,
    ): Result<Unit> = io {
        runCatching {
            val source = File(path)
            val requested = File(to)
            val dest = if (requested.isAbsolute) requested else File(source.parentFile, to)
            dest.parentFile?.mkdirs()
            if (dest.exists()) {
                dest.deleteRecursively()
            }
            check(source.renameTo(dest)) { "Failed to rename ${source.path} to ${dest.path}" }
        }
    }
}
