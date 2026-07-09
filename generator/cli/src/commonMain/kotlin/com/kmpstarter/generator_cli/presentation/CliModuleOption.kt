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

package com.kmpstarter.generator_cli.presentation

import com.kmpstarter.generator_domain.StarterModules

data class CliModuleOption(
    val module: StarterModules,
    val id: String,
    val label: String,
    val required: Boolean = false,
)
