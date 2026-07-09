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

enum class GenerateStep(val message: String) {
    GETTING_SOURCE_CODE("Getting source code"),
    EXTRACTING_SOURCE_CODE("Extracting source code"),
    REMOVING_TOOLING("Removing tooling modules"),
    CONFIGURING_WORKFLOWS("Configuring GitHub workflows"),
    CONFIGURING_MODULES("Configuring modules"),
    CONFIGURING_GRADLE_PLUGINS("Configuring Gradle plugins"),
    CONFIGURING_FEATURE("Configuring feature module"),
    CONFIGURING_PROJECT_NAME("Configuring project name"),
    CONFIGURING_PACKAGE_NAME("Configuring package name"),
    CREATING_STARTER_JSON("Creating starter.json"),
    PACKAGING_ZIP("Packaging project zip"),
    CLEANING_UP("Cleaning up temporary files"),
    SAVING_ZIP("Saving zip file"),
    EXTRACTING_OUTPUT("Extracting project"),
}

fun interface GenerateProgress {
    fun onStep(step: GenerateStep)
}
