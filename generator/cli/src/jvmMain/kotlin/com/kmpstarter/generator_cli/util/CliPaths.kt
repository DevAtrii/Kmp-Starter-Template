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

package com.kmpstarter.generator_cli.util

import java.nio.file.Path

object CliPaths {

    fun resolve(baseDir: String, path: String): String {
        val resolved = Path.of(path)
        return if (resolved.isAbsolute) {
            path
        } else {
            Path.of(baseDir, path).normalize().toString()
        }
    }

    fun extractDirForZip(zipPath: String): String {
        val fileName = Path.of(zipPath).fileName.toString()
        val folderName = fileName.substringBeforeLast('.', fileName)
        return Path.of(zipPath).parent?.resolve(folderName)?.normalize()?.toString()
            ?: folderName
    }
}
