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

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.OutputStream


@Suppress(names = ["EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING"])
actual class KmpFileManager(
    private val context: Context,
) {
    actual suspend fun saveFileToDownloadsFolder(
        fileName: String, folderName: String, fileContent: ByteArray, mimeType: String,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val resolver = context.contentResolver
            val outputStream: OutputStream?

            val result = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ → Scoped Storage using MediaStore
                val contentValues = ContentValues().apply {
                    put(MediaStore.Downloads.DISPLAY_NAME, fileName)
                    put(MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(
                        MediaStore.Downloads.RELATIVE_PATH,
                        Environment.DIRECTORY_DOWNLOADS + File.separator + folderName
                    )
                    put(MediaStore.Downloads.IS_PENDING, 1)
                }

                val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                if (uri != null) {
                    outputStream = resolver.openOutputStream(uri)

                    outputStream?.use {
                        it.write(fileContent)
                    }

                    // Mark file as finished
                    contentValues.clear()
                    contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, contentValues, null, null)
                    true
                } else false

            } else {
                // Android 9 and below → Write directly to Downloads folder
                val downloadsDir =
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                val targetDir = File(downloadsDir, folderName)
                if (!targetDir.exists()) targetDir.mkdirs()

                val file = File(targetDir, fileName + "." + mimeType.substringAfterLast("/"))
                outputStream = FileOutputStream(file)
                outputStream.use {
                    it.write(fileContent)
                }
                true
            }

            if (!result) throw Exception("Failed to save file")


            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    actual suspend fun saveFileIn(fileName: String, fileContent: ByteArray): Result<Unit> {
        TODO("Not yet implemented on Android")
    }

    actual suspend fun readFileFromCache(
        fileName: String,
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, fileName)

            if (!file.exists()) {
                throw FileNotFoundException("Cache file not found: $fileName")
            }

            val bytes = file.readBytes()
            Result.success(bytes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveFileInCache(
        fileName: String,
        fileContent: ByteArray,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, fileName)

            file.outputStream().use { output ->
                output.write(fileContent)
                output.flush()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    actual companion object

}



















