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

import com.kmpstarter.generator_data.CwdFileManager
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FileManagerImplTest {

    @Test
    fun zipRoundTripPreservesFileContents() = runBlocking {
        val root = Files.createTempDirectory("fm-zip")
        val src = root.resolve("src")
        Files.createDirectories(src.resolve("nested"))
        Files.writeString(src.resolve("hello.txt"), "hello")
        Files.writeString(src.resolve("nested/inner.txt"), "inner")

        val fm = FileManagerImpl()
        val zipBytes = fm.createZip(src.toString()).getOrThrow()
        val out = root.resolve("out")
        val zipFile = root.resolve("pack.zip")
        Files.write(zipFile, zipBytes)
        fm.extractZip(zipFile.toString(), out.toString()).getOrThrow()

        assertEquals("hello", Files.readString(out.resolve("hello.txt")))
        assertEquals("inner", Files.readString(out.resolve("nested/inner.txt")))
    }

    @Test
    fun extractZipRejectsPathTraversal() = runBlocking {
        val root = Files.createTempDirectory("fm-slip")
        val zipFile = root.resolve("evil.zip")
        ZipOutputStream(Files.newOutputStream(zipFile)).use { zip ->
            zip.putNextEntry(ZipEntry("../evil.txt"))
            zip.write("pwned".toByteArray())
            zip.closeEntry()
        }

        val fm = FileManagerImpl()
        val result = fm.extractZip(zipFile.toString(), root.resolve("out").toString())
        assertTrue(result.isFailure)
        return@runBlocking
    }

    @Test
    fun deleteMissingPathSucceeds() = runBlocking {
        val fm = FileManagerImpl()
        val missing = Files.createTempDirectory("fm-del").resolve("nope")
        fm.delete(missing.toString()).getOrThrow()
    }

    @Test
    fun writeReadAndRenameFile() = runBlocking {
        val dir = Files.createTempDirectory("fm-rw")
        val fm = CwdFileManager(dir.toString())
        val path = dir.resolve("a.txt").toString()
        fm.writeFile(path, "alpha".toByteArray()).getOrThrow()
        assertEquals("alpha", fm.getFile(path).getOrThrow().decodeToString())
        fm.rename(path, "b.txt").getOrThrow()
        assertEquals("alpha", fm.getFile(dir.resolve("b.txt").toString()).getOrThrow().decodeToString())
    }
}
