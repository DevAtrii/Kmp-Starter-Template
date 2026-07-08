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
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import kotlin.io.path.deleteIfExists
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.isRegularFile
import kotlin.io.path.outputStream
import kotlin.io.path.readBytes
import kotlin.io.path.writeBytes

private suspend inline fun <T> io(
    crossinline block: () -> T,
): T = withContext(Dispatchers.IO) {
    block()
}

actual class FileManagerImpl : StarterProjectFileManager {

    actual override fun getCurrentDir(): FolderPath =
        System.getProperty("user.dir")

    actual override suspend fun writeFile(
        path: FilePath,
        content: ByteArray,
    ): Result<Unit> = io {
        runCatching {
            val file = java.nio.file.Path.of(path)
            file.parent?.let(Files::createDirectories)
            file.writeBytes(content)
        }
    }

    actual override suspend fun getFilesRecursively(
        path: FolderPath,
    ): List<FilePath> = io {
        Files.walk(java.nio.file.Path.of(path))
            .use { stream ->
                stream.filter { it.isRegularFile() }
                    .map(java.nio.file.Path::toString)
                    .toList()
            }
    }

    actual override suspend fun mkDirs(
        path: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            val path = Files.createDirectories(java.nio.file.Path.of(path))
        }
    }

    actual override suspend fun getDirectoriesRecursively(
        path: FolderPath,
    ): List<FolderPath> = io {
        Files.walk(java.nio.file.Path.of(path))
            .use { stream ->
                stream.filter { it.isDirectory() }
                    .map(java.nio.file.Path::toString)
                    .toList()
            }
    }

    actual override suspend fun moveFiles(
        path: FolderPath,
        to: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            val source = java.nio.file.Path.of(path)
            val destination = java.nio.file.Path.of(to)

            Files.createDirectories(destination)

            Files.list(source).use { stream ->
                stream.forEach { child ->
                    Files.move(
                        child,
                        destination.resolve(child.fileName),
                        StandardCopyOption.REPLACE_EXISTING
                    )
                }
            }
        }
    }

    actual override suspend fun getFiles(
        path: FolderPath,
    ): List<FilePath> = io {
        Files.list(java.nio.file.Path.of(path))
            .use { stream ->
                stream.filter { it.isRegularFile() }
                    .map(java.nio.file.Path::toString)
                    .toList()
            }
    }

    actual override suspend fun getFile(
        path: FilePath,
    ): Result<ByteArray> = io {
        runCatching {
            java.nio.file.Path.of(path).readBytes()
        }
    }

    actual override suspend fun delete(
        path: Path,
    ): Result<Unit> = io {
        runCatching {
            val target = java.nio.file.Path.of(path)

            if (!target.exists()) return@runCatching

            Files.walk(target)
                .sorted(Comparator.reverseOrder())
                .forEach {
                    it.deleteIfExists()
                }
        }
    }

    actual override suspend fun extractZip(
        path: FilePath,
        output: FolderPath,
    ): Result<Unit> = io {
        runCatching {
            ZipInputStream(java.nio.file.Path.of(path).inputStream()).use { zip ->
                var entry = zip.nextEntry

                while (entry != null) {
                    val outputPath = java.nio.file.Path.of(output).resolve(entry.name)

                    if (entry.isDirectory) {
                        Files.createDirectories(outputPath)
                    } else {
                        outputPath.parent?.let(Files::createDirectories)
                        outputPath.outputStream().use { zip.copyTo(it) }
                    }

                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        }
    }

    actual override suspend fun createZip(
        path: Path,
    ): Result<ByteArray> = io {
        runCatching {
            val root = java.nio.file.Path.of(path)
            val output = ByteArrayOutputStream()

            ZipOutputStream(output).use { zip ->
                Files.walk(root).forEach { file ->
                    if (file.isDirectory()) return@forEach

                    val entryName = root
                        .relativize(file)
                        .toString()
                        .replace('\\', '/')

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
            val source = java.nio.file.Path.of(path)

            val path = Files.move(
                source,
                source.resolveSibling(to),
                StandardCopyOption.REPLACE_EXISTING
            )
        }
    }
}