import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Exec
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.bundling.Jar
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl


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

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.android.lint)
    // from build logic
    alias(libs.plugins.kotlin.serialization)
    id(libs.plugins.build.koin.core.get().pluginId)
    id(libs.plugins.build.common.get().pluginId)
}

kotlin {
    // adding this so that we don't get error because of build-logic plugins are applied
    android {
        namespace = "com.kmpstarter.generator_cli"
        compileSdk {
            version = release(version = libs.versions.android.compileSdk.get().toInt())
        }
        minSdk {
            version = release(libs.versions.android.minSdk.get().toInt())
        }
    }
    jvm {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        mainRun {
            mainClass.set("com.kmpstarter.generator_cli.MainKt")
        }
        binaries {
            executable {
                mainClass.set("com.kmpstarter.generator_cli.MainKt")
            }
        }
    }
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlin.stdlib)
            api(projects.generator.domain)
        }

        jsMain.dependencies {
            implementation(libs.wrappers.browser)
        }

        jvmMain.dependencies {
            implementation(projects.generator.data)
            implementation(libs.clikt)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.androidx.lifecycle.viewmodel)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        jvmTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(projects.generator.data)
            implementation(libs.kotlinx.coroutines.core)
        }

    }

}

tasks.withType<JavaExec>().configureEach {
    if (name == "jvmRun" || name == "runJvm") {
        standardInput = System.`in`
    }
}

val starterCliVersion: String =
    "${libs.versions.cli.version.major.get()}." +
        "${libs.versions.cli.version.minor.get()}." +
        libs.versions.cli.version.patch.get()

val starterCliNpmDir = layout.projectDirectory.dir("npm")
val starterCliNpmPackageDir = layout.projectDirectory.dir("npm-package")
val cliVersionGeneratedDir = layout.buildDirectory.dir("generated/cliVersion")

val generateCliVersion = tasks.register("generateCliVersion") {
    group = "build"
    description = "Generates CliVersion.kt from gradle/libs.versions.toml cli-version-*"

    val version = starterCliVersion
    val outputDir = cliVersionGeneratedDir
    inputs.property("starterCliVersion", version)
    outputs.dir(outputDir)

    doLast {
        val file = outputDir.get().file("com/kmpstarter/generator_cli/CliVersion.kt").asFile
        file.parentFile.mkdirs()
        file.writeText(
            """
            |package com.kmpstarter.generator_cli
            |
            |object CliVersion {
            |    const val VALUE = "$version"
            |}
            |
            """.trimMargin(),
        )
    }
}

kotlin {
    sourceSets.named("jvmMain") {
        kotlin.srcDir(cliVersionGeneratedDir)
    }
}

tasks.named("compileKotlinJvm").configure {
    dependsOn(generateCliVersion)
}

val starterCliFatJar = tasks.register<Jar>("starterCliFatJar") {
    group = "distribution"
    description = "Builds an executable fat JAR for the CLI npm package"

    archiveBaseName.set("starter-cli")
    archiveVersion.set(starterCliVersion)
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    val jvmCompilation = kotlin.jvm().compilations.getByName("main")
    dependsOn(jvmCompilation.compileTaskProvider)

    from(jvmCompilation.output.allOutputs)
    from({
        jvmCompilation.runtimeDependencyFiles
            .filter { it.exists() }
            .map { file -> if (file.isDirectory) file else zipTree(file) }
    })

    manifest {
        attributes["Main-Class"] = "com.kmpstarter.generator_cli.MainKt"
    }
}

tasks.register("syncStarterCliNpmVersion") {
    group = "distribution"
    description = "Syncs npm package.json version with gradle/libs.versions.toml cli-version-*"

    val version = starterCliVersion
    inputs.property("starterCliVersion", version)
    outputs.file(starterCliNpmDir.file("package.json"))

    doLast {
        val packageJson = starterCliNpmDir.file("package.json").asFile
        val updated = packageJson.readText().replace(
            Regex(""""version"\s*:\s*"[^"]*""""),
            """"version": "$version"""",
        )
        packageJson.writeText(updated)
    }
}

val assembleStarterCliNpm = tasks.register<Copy>("assembleStarterCliNpm") {
    group = "distribution"
    description = "Assembles the @devatrii/starter npm package with fat JAR"

    dependsOn(starterCliFatJar, "syncStarterCliNpmVersion")

    into(starterCliNpmPackageDir)

    from(starterCliNpmDir) {
        include("package.json", "bin/**")
    }

    from(starterCliFatJar) {
        into("lib")
        rename { "starter-cli.jar" }
    }
}

tasks.register<Exec>("starterCliNpmPack") {
    group = "distribution"
    description = "Runs npm pack on the assembled CLI package (local tarball for testing)"
    dependsOn(assembleStarterCliNpm)
    workingDir = starterCliNpmPackageDir.asFile
    commandLine(
        "npm",
        "pack",
        "--ignore-scripts",
    )
}

tasks.register<Exec>("starterCliNpmPublish") {
    group = "distribution"
    description = "Publishes the assembled CLI package to the npm registry (requires NODE_AUTH_TOKEN)"
    dependsOn(assembleStarterCliNpm)
    workingDir = starterCliNpmPackageDir.asFile
    commandLine(
        "npm",
        "publish",
        "--access",
        "public",
        "--ignore-scripts",
    )
}