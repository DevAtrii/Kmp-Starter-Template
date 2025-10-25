/*
 *
 *  *
 *  *  * Copyright (c) 2025
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

plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.bundles.gradle.plugins)
    compileOnly("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.2.20")
}

gradlePlugin {
    plugins {
        registerPlugin(
            id = "com.kmpstarter.plugins.composemultiplatform",
            implementationClass = "ComposeMultiplatformPlugin",
        )
        registerPlugin(
            id = "com.kmpstarter.plugins.koin",
            implementationClass = "KoinPlugin",
        )
        registerPlugin(
            id = "com.kmpstarter.plugins.koincompose",
            implementationClass = "KoinComposePlugin",
        )
        registerPlugin(
            id = "com.kmpstarter.plugins.common",
            implementationClass = "CommonPlugin",
        )
    }
}

fun NamedDomainObjectContainer<PluginDeclaration>.registerPlugin(
    id: String,
    implementationClass: String,
) {
    // For simplicity, we use id as the name of the plugin declaration
    register(id) {
        this.id = id
        this.implementationClass = implementationClass
    }
}