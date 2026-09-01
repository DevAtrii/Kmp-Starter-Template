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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CliPathsTest {

    @Test
    fun relativePathJoinsBaseDir() {
        val resolved = CliPaths.resolve("/tmp/cwd", "out/app.zip")
        assertTrue(resolved.endsWith("out/app.zip") || resolved.endsWith("out\\app.zip"))
        assertTrue(resolved.contains("cwd"))
    }

    @Test
    fun absolutePathIsUnchanged() {
        assertEquals("/tmp/app.zip", CliPaths.resolve("/other", "/tmp/app.zip"))
    }

    @Test
    fun extractDirUsesZipFileNameWithoutExtension() {
        val dir = CliPaths.extractDirForZip("/tmp/notes-app.zip")
        assertEquals("/tmp/notes-app", dir)
    }
}
