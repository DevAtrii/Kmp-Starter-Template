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


/**
 * Metadata describing a file.
 *
 * Values that are unavailable on current platform are represented as `null`.
 */
data class StarterFile(

    /** Platform-specific path or identifier used to access file. */
    val path: FilePath,

    /** File name without parent directories. */
    val name: String,

    /** File extension without leading dot (for example, `pdf`). */
    val extension: FileExtension,

    /** MIME type, if available. */
    val mimeType: FileMimeType?,

    /** File size in bytes, if available. */
    val sizeBytes: Long?,

    /** File creation timestamp in Unix milliseconds, if available. */
    val createdAtMillis: Long?,

    /** Last modification timestamp in Unix milliseconds, if available. */
    val modifiedAtMillis: Long?,
)