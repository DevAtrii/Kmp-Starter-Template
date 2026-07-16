package com.kmpstarter.utils.files

import com.kmpstarter.utils.starter.ExperimentalStarterApi

@ExperimentalStarterApi
internal val StarterFileManager.Companion.TAG: String
    get() = "StarterFileManager"

/** Generic path. */
typealias Path = String

/** File name without extension. */
typealias FileName = String

/** Platform-specific file path or identifier. */
typealias FilePath = String

/** Relative folder path. */
typealias FolderPath = String

/** MIME type (for example, `application/pdf`). */
typealias FileMimeType = String

/** File extension without leading dot (for example, `pdf`). */
typealias FileExtension = String

/** Raw file contents. */
typealias FileContent = ByteArray

/**
 * Cross-platform file management API.
 *
 * Provides common operations for reading and writing files across supported
 * platforms while hiding platform-specific storage implementations.
 *
 * All operations return [Result] to represent either a successful outcome
 * or a platform-specific failure.
 *
 * for more advance file io please use [FileKit](https://filekit.mintlify.app/introduction) library
 */
@ExperimentalStarterApi
expect class StarterFileManager {

    companion object {}

    /**
     * Prompts user to choose where file should be saved.
     *
     * Implementations may display a system file picker or save dialog.
     *
     * On Android, requires a host `[androidx.activity.ComponentActivity]`. Use
     * `[com.kmpstarter.ui_utils.files.rememberStarterFileManager]` in Compose instead of
     * the Koin singleton when calling this method.
     *
     * @param suggestedName Default file name without extension.
     * @param extension File extension without leading dot.
     * @param content File contents.
     * @param mimeType MIME type used by platform when available.
     */
    suspend fun saveFileIn(
        suggestedName: FileName,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<Unit>

    /**
     * Saves a file into the platform Downloads directory when supported.
     *
     * If [folderPath] is provided, the file is created inside that subdirectory.
     * Missing directories may be created automatically depending on platform.
     *
     * ### Platform behavior
     * - **Android:** Saves the file into the user's public **Downloads** directory.
     * - **iOS:** iOS does not allow apps to write directly to the user's public
     *   Downloads folder. Files are instead saved to the app's Documents directory:
     *   `Documents/...`.
     *
     * For files that should be accessible outside your app, prefer [saveFileIn],
     * which lets the user choose the destination using the system file picker.
     *
     * For temporary or app-private files, prefer [saveInCache],
     * [getFilesFromCache], and [readFromCache].
     *
     * @param file File name without extension.
     * @param folderPath Optional relative folder path.
     * @param extension File extension without leading dot.
     * @param content File contents.
     * @param mimeType MIME type associated with the file.
     */
    suspend fun saveFileIntoDownloads(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<StarterFile>

    /**
     * Lists files from Downloads directory.
     *
     * If [path] is `null`, files from Downloads root are returned.
     * Otherwise, files from given relative subdirectory are returned.
     */
    suspend fun getFilesFromDownloads(
        path: FolderPath?,
    ): Result<List<StarterFile>>


    /**
     * Gets a single file from Downloads by [file] name (without extension).
     *
     * If [path] is `null`, the Downloads root is searched.
     * Otherwise, the given relative subdirectory is searched.
     *
     * @param file File name without extension.
     * @param path Optional relative folder inside Downloads.
     */
    suspend fun getFileFromDownloads(
        file: FileName,
        path: FolderPath?,
    ): Result<StarterFile>

    /**
     * Reads a file from Downloads using its platform [FilePath].
     *
     * Pass the [StarterFile.path] value returned by [getFilesFromDownloads].
     * [path] must not be null or blank.
     *
     * @param path Platform-specific Downloads file path or content URI.
     * @return File metadata paired with raw file contents.
     */
    suspend fun readFromDownloads(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>>

    /**
     * Deletes a file from Downloads using its platform [FilePath].
     *
     * Pass the [StarterFile.path] value returned by [getFilesFromDownloads].
     *
     * @param path Platform-specific Downloads file path or content URI.
     */
    suspend fun deleteFromDownloads(
        path: FilePath,
    ): Result<Unit>

    /**
     * Renames a file in Downloads, keeping its existing extension.
     *
     * Pass the [StarterFile.path] value returned by [getFilesFromDownloads].
     *
     * @param path Platform-specific Downloads file path or content URI.
     * @param to New file name without extension.
     */
    suspend fun renameFromDownloads(
        path: FilePath,
        to: FileName,
    ): Result<StarterFile>

    /**
     * Lists files from cache directory.
     *
     * @param path Relative folder inside cache directory.
     */
    suspend fun getFilesFromCache(
        path: FolderPath,
    ): Result<List<StarterFile>>

    /**
     * Gets a single file from cache by [file] name (without extension).
     *
     * If [path] is `null`, the cache root is searched.
     * Otherwise, the given relative subdirectory is searched.
     *
     * @param file File name without extension.
     * @param path Optional relative folder inside cache directory.
     */
    suspend fun getFileFromCache(
        file: FileName,
        path: FolderPath?,
    ): Result<StarterFile>

    /**
     * Reads file contents from cache directory.
     *
     * @param path Relative path to cached file.
     * @return File metadata paired with raw file contents.
     */
    suspend fun readFromCache(
        path: FilePath,
    ): Result<Pair<StarterFile, FileContent>>

    /**
     * Saves file into cache directory.
     *
     * If [folderPath] is provided, file is created inside that subdirectory.
     * Missing directories may be created automatically depending on platform.
     *
     * @param file File name without extension.
     * @param folderPath Optional relative folder inside cache directory.
     * @param extension File extension without leading dot.
     * @param content File contents.
     * @param mimeType MIME type associated with file.
     */
    suspend fun saveInCache(
        file: FileName,
        folderPath: FolderPath?,
        extension: FileExtension,
        content: ByteArray,
        mimeType: FileMimeType,
    ): Result<StarterFile>

    /**
     * Deletes a file from cache using its relative [FilePath].
     *
     * Pass a relative path under the cache directory, for example
     * `folder/file.txt`, matching [readFromCache].
     *
     * @param path Relative path to cached file.
     */
    suspend fun deleteFromCache(
        path: FilePath,
    ): Result<Unit>

    /**
     * Renames a file in cache, keeping its existing extension.
     *
     * Pass a relative path under the cache directory, for example
     * `folder/file.txt`, matching [readFromCache].
     *
     * @param path Relative path to cached file.
     * @param to New file name without extension.
     */
    suspend fun renameFromCache(
        path: FilePath,
        to: FileName,
    ): Result<StarterFile>


    /**
     * Opens the platform share sheet for a file.
     *
     * Pass a platform [Path]:
     * - Downloads: [StarterFile.path] from [getFilesFromDownloads] (absolute path or content URI)
     * - Cache: relative path under cache, matching [readFromCache]
     *
     * ### Android requirements
     * - Requires a host [androidx.activity.ComponentActivity]. Use
     *   [com.kmpstarter.ui_utils.files.rememberStarterFileManager] in Compose instead of
     *   the Koin singleton when calling this method.
     * - Sharing local/cache file paths requires the **host app** to declare a
     *   [androidx.core.content.FileProvider] with authority
     *   `${applicationId}.starter.fileprovider` and a paths XML (for example
     *   `res/xml/starter_file_paths.xml`). This library does **not** merge that
     *   provider; the host decides whether to include it.
     * - `content://` URIs (for example MediaStore Downloads) do not need FileProvider.
     *
     * @param path Platform-specific file path or content URI.
     */
    suspend fun shareFile(
        path: Path,
    ): Result<Unit>

    /**
     * Opens a file with the system app picker / default handler.
     *
     * Pass a platform [Path]:
     * - Downloads: [StarterFile.path] from [getFilesFromDownloads] (absolute path or content URI)
     * - Cache: relative path under cache, matching [readFromCache]
     *
     * The system decides which apps can open the file (Android `ACTION_VIEW` chooser /
     * iOS Open In menu).
     *
     * ### Android requirements
     * - Requires a host [androidx.activity.ComponentActivity]. Use
     *   [com.kmpstarter.ui_utils.files.rememberStarterFileManager] in Compose instead of
     *   the Koin singleton when calling this method.
     * - Opening local/cache file paths requires the **host app** to declare a
     *   [androidx.core.content.FileProvider] with authority
     *   `${applicationId}.starter.fileprovider` (same setup as [shareFile]).
     * - `content://` URIs (for example MediaStore Downloads) do not need FileProvider.
     *
     * @param path Platform-specific file path or content URI.
     */
    suspend fun openFile(
        path: Path,
    ): Result<Unit>
}
