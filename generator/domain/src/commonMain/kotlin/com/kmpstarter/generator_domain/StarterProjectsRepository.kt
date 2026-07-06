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

package com.kmpstarter.generator_domain

typealias ProjectZip = ByteArray

private const val DEFAULT_TARGET_MODULE = "composeApp"
private const val DEFAULT_PACKAGE_NAME = "com.kmpstarter"

interface StarterProjectsRepository {

    companion object {
        const val STARTER_FOLDER = ".starter"

        val TOOLING_SOURCE_CODE_FOLDERS = listOf(
            "docs",
            "generator",
            "site",
        )
        val TOOLING_SOURCE_CODE_FILES = listOf(
            "zensical.toml",
            "mkdocs.yml",
        )
        val TOOLING_SETTINGS_GRADLE_MODULES = listOf(
            ":generator:data",
            ":generator:domain",
            ":generator:web",
            ":generator:cli",
        )
    }

    suspend fun generate(project: StarterProject): Result<ProjectZip>

    suspend fun includeModule(
        module: StarterModules,
        mode: ProjectMode,
        packageName: String? = DEFAULT_PACKAGE_NAME,
        targetModule: String = DEFAULT_TARGET_MODULE,
    ): Result<Unit>

    suspend fun excludeModule(
        module: StarterModules,
        mode: ProjectMode,
        packageName: String? = DEFAULT_PACKAGE_NAME,
        targetModule: String = DEFAULT_TARGET_MODULE,
    ): Result<Unit>


}