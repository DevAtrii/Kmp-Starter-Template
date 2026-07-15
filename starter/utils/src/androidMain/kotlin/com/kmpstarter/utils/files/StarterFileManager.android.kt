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

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.kmpstarter.utils.starter.ExperimentalStarterApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import kotlin.coroutines.resume

private const val SAVE_FILE_IN_ACTIVITY_REQUIRED =
    "saveFileIn requires a ComponentActivity. Use rememberStarterFileManager() from " +
        "com.kmpstarter.ui_utils.files in Compose instead of Koin-injected StarterFileManager."

@OptIn(ExperimentalStarterApi::class)
actual class StarterFileManager(
    private val context: Context,
    private val activity: Activity?,
) {
    actual suspend fun saveFileIn(
        suggestedName: FileName,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.Main) {
        try {
            val componentActivity = activity as? ComponentActivity
                ?: return@withContext Result.failure(
                    IllegalStateException(SAVE_FILE_IN_ACTIVITY_REQUIRED),
                )

            val fileName = buildFileName(suggestedName, extension)
            val uri = suspendCancellableCoroutine<Uri?> { continuation ->
                val contract = ActivityResultContracts.CreateDocument(mimeType)
                val launcher = componentActivity.activityResultRegistry.register(
                    key = "starter_save_file_${System.nanoTime()}",
                    contract = contract,
                ) { resultUri ->
                    if (continuation.isActive) {
                        continuation.resume(resultUri)
                    }
                }

                continuation.invokeOnCancellation {
                    launcher.unregister()
                }

                launcher.launch(fileName)
            } ?: return@withContext Result.failure(
                IllegalStateException("User cancelled save dialog"),
            )

            withContext(Dispatchers.IO) {
                context.contentResolver.openOutputStream(uri)?.use { output ->
                    output.write(content)
                    output.flush()
                } ?: throw IllegalStateException("Unable to open output stream for $uri")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveFileIntoDownloads(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val fileName = buildFileName(file, extension)
            val saved = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                saveToDownloadsMediaStore(
                    fileName = fileName,
                    folderPath = folderPath,
                    content = content,
                    mimeType = mimeType,
                )
            } else {
                saveToDownloadsLegacy(
                    fileName = fileName,
                    folderPath = folderPath,
                    content = content,
                )
            }

            if (!saved) {
                return@withContext Result.failure(IllegalStateException("Failed to save file to Downloads"))
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun getFilesFromDownloads(
        path: FolderPath?,
    ): Result<List<StarterFile>> = withContext(Dispatchers.IO) {
        try {
            val files = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                queryDownloadsFromMediaStore(path)
            } else {
                listDownloadsLegacy(path)
            }
            Result.success(files)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun readFromDownloads(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>> = withContext(Dispatchers.IO) {
        try {
            if (path.isBlank()) {
                return@withContext Result.failure(IllegalArgumentException("Downloads file path is required"))
            }

            if (path.startsWith("content://")) {
                val uri = Uri.parse(path)
                val bytes = context.contentResolver.openInputStream(uri)?.use { input ->
                    input.readBytes()
                } ?: throw FileNotFoundException("Downloads file not found: $path")

                val starterFile = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    queryStarterFileByUri(uri)
                } else {
                    null
                } ?: buildStarterFileFromPath(path, bytes.size.toLong())

                Result.success(starterFile to bytes)
            } else {
                val file = File(path)
                if (!file.exists()) {
                    throw FileNotFoundException("Downloads file not found: $path")
                }
                val starterFile = file.toStarterFile()
                    ?: throw IllegalStateException("Unable to read Downloads file metadata: $path")
                Result.success(starterFile to file.readBytes())
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun getFilesFromCache(
        path: FolderPath,
    ): Result<List<StarterFile>> = withContext(Dispatchers.IO) {
        try {
            val directory = File(context.cacheDir, path)
            if (!directory.exists() || !directory.isDirectory) {
                return@withContext Result.success(emptyList())
            }

            Result.success(directory.listFiles()?.mapNotNull { it.toStarterFile() }.orEmpty())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun readFromCache(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>> = withContext(Dispatchers.IO) {
        try {
            val file = File(context.cacheDir, path)
            if (!file.exists()) {
                throw FileNotFoundException("Cache file not found: $path")
            }
            val starterFile = file.toStarterFile()
                ?: throw IllegalStateException("Unable to read cache file metadata: $path")
            Result.success(starterFile to file.readBytes())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    actual suspend fun saveInCache(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val directory = folderPath?.let { File(context.cacheDir, it) } ?: context.cacheDir
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val targetFile = File(directory, buildFileName(file, extension))
            targetFile.outputStream().use { output ->
                output.write(content)
                output.flush()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun saveToDownloadsMediaStore(
        fileName: String,
        folderPath: FolderPath?,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Boolean {
        val resolver = context.contentResolver
        val relativePath = buildDownloadsRelativePath(folderPath)

        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, relativePath)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }

        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
            ?: return false

        resolver.openOutputStream(uri)?.use { output ->
            output.write(content)
        } ?: return false

        contentValues.clear()
        contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
        resolver.update(uri, contentValues, null, null)
        return true
    }

    private fun saveToDownloadsLegacy(
        fileName: String,
        folderPath: FolderPath?,
        content: ByteArray,
    ): Boolean {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDir = folderPath?.let { File(downloadsDir, it) } ?: downloadsDir
        if (!targetDir.exists()) {
            targetDir.mkdirs()
        }

        val file = File(targetDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(content)
        }
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryDownloadsFromMediaStore(path: FolderPath?): List<StarterFile> {
        val resolver = context.contentResolver
        val relativePath = buildDownloadsRelativePath(path)
        val selection = "${MediaStore.Downloads.RELATIVE_PATH} LIKE ?"
        val selectionArgs = arrayOf("$relativePath%")

        val projection = arrayOf(
            MediaStore.Downloads._ID,
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.MIME_TYPE,
            MediaStore.Downloads.SIZE,
            MediaStore.Downloads.DATE_ADDED,
            MediaStore.Downloads.DATE_MODIFIED,
        )

        val files = mutableListOf<StarterFile>()
        resolver.query(
            MediaStore.Downloads.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            "${MediaStore.Downloads.DATE_MODIFIED} DESC",
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
            val addedColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)
            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val displayName = cursor.getString(nameColumn).orEmpty()
                val uri = Uri.withAppendedPath(MediaStore.Downloads.EXTERNAL_CONTENT_URI, id.toString())
                val (name, extension) = splitFileName(displayName)

                files += StarterFile(
                    path = uri.toString(),
                    name = name,
                    extension = extension,
                    mimeType = cursor.getString(mimeColumn),
                    sizeBytes = cursor.getLong(sizeColumn).takeIf { it >= 0 },
                    createdAtMillis = cursor.getLong(addedColumn).takeIf { it > 0 }?.times(1_000),
                    modifiedAtMillis = cursor.getLong(modifiedColumn).takeIf { it > 0 }?.times(1_000),
                )
            }
        }

        return files
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun queryStarterFileByUri(uri: Uri): StarterFile? {
        val resolver = context.contentResolver
        val projection = arrayOf(
            MediaStore.Downloads.DISPLAY_NAME,
            MediaStore.Downloads.MIME_TYPE,
            MediaStore.Downloads.SIZE,
            MediaStore.Downloads.DATE_ADDED,
            MediaStore.Downloads.DATE_MODIFIED,
        )

        resolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (!cursor.moveToFirst()) return null

            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DISPLAY_NAME)
            val mimeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.MIME_TYPE)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.SIZE)
            val addedColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_ADDED)
            val modifiedColumn = cursor.getColumnIndexOrThrow(MediaStore.Downloads.DATE_MODIFIED)

            val displayName = cursor.getString(nameColumn).orEmpty()
            val (name, extension) = splitFileName(displayName)

            return StarterFile(
                path = uri.toString(),
                name = name,
                extension = extension,
                mimeType = cursor.getString(mimeColumn),
                sizeBytes = cursor.getLong(sizeColumn).takeIf { it >= 0 },
                createdAtMillis = cursor.getLong(addedColumn).takeIf { it > 0 }?.times(1_000),
                modifiedAtMillis = cursor.getLong(modifiedColumn).takeIf { it > 0 }?.times(1_000),
            )
        }

        return null
    }

    private fun buildStarterFileFromPath(path: FilePath, sizeBytes: Long): StarterFile {
        val fileName = path.substringAfterLast('/')
        val (name, extension) = splitFileName(fileName)
        return StarterFile(
            path = path,
            name = name,
            extension = extension,
            mimeType = null,
            sizeBytes = sizeBytes,
            createdAtMillis = null,
            modifiedAtMillis = null,
        )
    }

    private fun listDownloadsLegacy(path: FolderPath?): List<StarterFile> {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetDir = path?.let { File(downloadsDir, it) } ?: downloadsDir
        if (!targetDir.exists() || !targetDir.isDirectory) {
            return emptyList()
        }

        return targetDir.listFiles()?.mapNotNull { it.toStarterFile() }.orEmpty()
    }

    private fun File.toStarterFile(): StarterFile? {
        if (!isFile) return null
        val (name, extension) = splitFileName(name)
        return StarterFile(
            path = absolutePath,
            name = name,
            extension = extension,
            mimeType = null,
            sizeBytes = length(),
            createdAtMillis = null,
            modifiedAtMillis = lastModified(),
        )
    }

    private fun buildFileName(file: FileName, extension: FileExtension): String {
        return if (extension.isBlank()) file else "$file.$extension"
    }

    private fun buildDownloadsRelativePath(folderPath: FolderPath?): String {
        return if (folderPath.isNullOrBlank()) {
            "${Environment.DIRECTORY_DOWNLOADS}${File.separator}"
        } else {
            "${Environment.DIRECTORY_DOWNLOADS}${File.separator}$folderPath${File.separator}"
        }
    }

    private fun splitFileName(fileName: String): Pair<String, String> {
        val dotIndex = fileName.lastIndexOf('.')
        return if (dotIndex <= 0 || dotIndex == fileName.length - 1) {
            fileName to ""
        } else {
            fileName.substring(0, dotIndex) to fileName.substring(dotIndex + 1)
        }
    }

    actual companion object
}
