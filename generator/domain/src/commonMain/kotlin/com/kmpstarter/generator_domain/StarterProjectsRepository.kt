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



interface StarterProjectsRepository {

    companion object {

        const val DEFAULT_TARGET_MODULE = "composeApp"
        const val DEFAULT_PACKAGE_NAME = "com.kmpstarter"
        const val STARTER_FOLDER = ".starter"
        const val STARTER_JSON_FILE = "starter.json"

        val TOOLING_SOURCE_CODE_FOLDERS = listOf(
            "docs",
            "generator",
            "site",
            ".git"
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
        /** when ProjectMode.LIB then keep these modules & add rest as library*/
        val LIB_MODE_DELETABLE_MODULES = listOf(
            ":features:analytics:data",
            ":features:analytics:domain",
            ":features:locale",
            ":features:navigation",
            ":features:notifications:core",
            ":features:notifications:local",
            ":features:notifications:push",
            ":features:purchases:data",
            ":features:purchases:domain",
            ":features:purchases:presentation",
            ":features:remote_config:data",
            ":features:remote_config:domain",
            ":features:remote_config:presentation",
            ":starter:core",
            ":starter:native:bindings",
            ":starter:ui:utils",
            ":starter:ui:layouts",
            ":starter:ui:components",
            ":starter:utils",
        )

        /**Modules that aren't published on maven but can be added to project regardless of ProjectMode**/
        val LOCAL_MODULES = listOf(
            ":features:core:data",
            ":features:core:domain",
            ":features:core:presentation",
            ":features:database",
            ":features:resources",
        )
    }

    suspend fun generate(project: StarterProject): Result<ProjectZip>

    suspend fun includeModule(
        workingDir: String,
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