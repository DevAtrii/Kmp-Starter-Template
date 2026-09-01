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

package com.kmpstarter.generator_data

import com.kmpstarter.generator_data.impl.FileManagerImpl
import com.kmpstarter.generator_data.interfaces.FolderPath
import com.kmpstarter.generator_data.interfaces.StarterProjectFileManager

internal class CwdFileManager(
    private val cwd: String,
    private val delegate: StarterProjectFileManager = FileManagerImpl(),
) : StarterProjectFileManager by delegate {
    override fun getCurrentDir(): FolderPath = cwd
}
