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
import com.kmpstarter.generator_data.impl.zip.StoreZip

actual class FileManagerImpl : StarterProjectFileManager {
    private val files = mutableMapOf<String, ByteArray>()
    private val dirs = mutableSetOf("/")

    actual override fun getCurrentDir(): FolderPath = "/work"

    actual override suspend fun writeFile(
        path: FilePath,
        content: ByteArray,
    ): Result<Unit> = runCatching {
        val file = norm(path)
        mkDir(parentOf(file))
        files[file] = content
    }

    actual override suspend fun getFilesRecursively(path: FolderPath): List<FilePath> {
        val root = norm(path)
        return files.keys.filter { it == root || it.startsWith("$root/") }.sorted()
    }

    actual override suspend fun mkDirs(path: FolderPath): Result<Unit> = runCatching {
        mkDir(norm(path))
    }

    actual override suspend fun getDirectoriesRecursively(path: FolderPath): List<FolderPath> {
        val root = norm(path)
        return dirs.filter { it == root || it.startsWith("$root/") }.sorted()
    }

    actual override suspend fun moveFiles(
        path: FolderPath,
        to: FolderPath,
    ): Result<Unit> = runCatching {
        val source = norm(path)
        val dest = norm(to)
        mkDir(dest)
        val children = files.filterKeys { parentOf(it) == source }
        children.forEach { (from, bytes) ->
            val name = from.substringAfterLast('/')
            files.remove(from)
            files["$dest/$name"] = bytes
        }
        dirs.filter { parentOf(it) == source }.forEach { child ->
            val name = child.substringAfterLast('/')
            dirs.remove(child)
            mkDir("$dest/$name")
        }
    }

    actual override suspend fun getFiles(path: FolderPath): List<FilePath> {
        val root = norm(path)
        return files.keys.filter { parentOf(it) == root }.sorted()
    }

    actual override suspend fun getFile(path: FilePath): Result<ByteArray> = runCatching {
        files[norm(path)] ?: error("No such file: $path")
    }

    actual override suspend fun delete(path: Path): Result<Unit> = runCatching {
        val target = norm(path)
        files.keys.filter { it == target || it.startsWith("$target/") }.toList().forEach(files::remove)
        dirs.filter { it == target || it.startsWith("$target/") }.toList().forEach(dirs::remove)
        dirs.add("/")
    }

    actual override suspend fun extractZip(
        path: FilePath,
        output: FolderPath,
    ): Result<Unit> = runCatching {
        val zipBytes = files[norm(path)] ?: error("No such file: $path")
        val root = norm(output)
        mkDir(root)
        StoreZip.unpack(zipBytes).forEach { (entry, bytes) ->
            val dest = norm("$root/$entry")
            require(dest == root || dest.startsWith("$root/")) { "Invalid ZIP entry: $entry" }
            mkDir(parentOf(dest))
            files[dest] = bytes
        }
    }

    actual override suspend fun createZip(path: Path): Result<ByteArray> = runCatching {
        val root = norm(path)
        val payload = files
            .filterKeys { it == root || it.startsWith("$root/") }
            .mapKeys { (file, _) -> file.removePrefix("$root/").ifEmpty { file.substringAfterLast('/') } }
        StoreZip.pack(payload)
    }

    actual override suspend fun rename(
        path: Path,
        to: String,
    ): Result<Unit> = runCatching {
        val source = norm(path)
        val dest = if (to.startsWith("/") || to.contains("/")) {
            norm(to)
        } else {
            val parent = parentOf(source)
            if (parent == "/") "/$to" else "$parent/$to"
        }
        if (files.containsKey(source)) {
            files[dest] = files.remove(source)!!
            mkDir(parentOf(dest))
            return@runCatching
        }
        val prefix = "$source/"
        files.filterKeys { it == source || it.startsWith(prefix) }.keys.toList().forEach { from ->
            val relative = from.removePrefix(source).trimStart('/')
            val next = if (relative.isEmpty()) dest else "$dest/$relative"
            files[next] = files.remove(from)!!
            mkDir(parentOf(next))
        }
        dirs.filter { it == source || it.startsWith(prefix) }.toList().forEach { from ->
            dirs.remove(from)
            val relative = from.removePrefix(source).trimStart('/')
            mkDir(if (relative.isEmpty()) dest else "$dest/$relative")
        }
    }

    private fun mkDir(path: String) {
        var current = ""
        path.split('/').filter { it.isNotEmpty() }.forEach { part ->
            current += "/$part"
            dirs += current
        }
        dirs += "/"
    }

    private fun parentOf(path: String): String {
        val trimmed = path.trimEnd('/')
        val index = trimmed.lastIndexOf('/')
        return if (index <= 0) "/" else trimmed.substring(0, index)
    }

    private fun norm(path: String): String {
        val parts = mutableListOf<String>()
        path.replace('\\', '/').split('/').forEach { part ->
            when (part) {
                "", "." -> Unit
                ".." -> if (parts.isNotEmpty()) parts.removeLast()
                else -> parts += part
            }
        }
        return if (parts.isEmpty()) "/" else "/${parts.joinToString("/")}"
    }
}
