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
import com.kmpstarter.generator_domain.ProjectMode
import com.kmpstarter.generator_domain.StarterJson
import com.kmpstarter.generator_domain.StarterModules
import com.kmpstarter.generator_domain.StarterProject
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class GenerateProjectTest {

    @Test
    fun libModeCreateRenamesAppStripsToolingAndWritesStarterJson() = runBlocking {
        val generated = generate(
            StarterProject(
                projectName = "NotesApp",
                packageName = "com.example.notes",
                mode = ProjectMode.LIB,
                featureName = "notes",
                includeWorkflows = true,
                modules = requiredPlus(),
            ),
        )

        val settings = generated.read("settings.gradle.kts")
        assertContains(settings, """rootProject.name = "NotesApp"""")
        assertFalse("include(\":generator:cli\")" in settings)
        assertFalse("starterLibsLocal" in settings)
        assertContains(settings, """include(":features:notes:data")""")
        assertFalse("include(\":features:your-feature:data\")" in settings)

        assertFalse(generated.exists("docs/getting-started.md"))
        assertFalse(generated.exists("generator/cli/readme.md"))
        assertFalse(generated.exists("site/index.html"))
        assertFalse(generated.exists("zensical.toml"))
        assertFalse(generated.exists("mkdocs.yml"))
        assertFalse(generated.exists(".github/workflows/publish-maven.yml"))
        assertTrue(generated.exists(".github/workflows/ci.yml"))

        assertFalse(generated.exists("starter/core/build.gradle.kts"))
        assertTrue(generated.exists("features/core/data/build.gradle.kts"))
        assertTrue(generated.exists("features/notes/data/build.gradle.kts"))

        val composeApp = generated.read("composeApp/build.gradle.kts")
        assertContains(composeApp, "implementation(libs.starter.core)")
        assertContains(composeApp, "implementation(projects.features.core.data)")
        assertContains(composeApp, "implementation(projects.features.notes.data)")
        assertContains(composeApp, "namespace = \"com.example.notes\"")

        val initKoin = generated.read(
            "composeApp/src/commonMain/kotlin/com/example/notes/core/di/InitKoin.kt",
        )
        assertContains(initKoin, "package com.example.notes.core.di")
        assertContains(initKoin, "featureNotesDataModule")
        assertContains(initKoin, "import com.kmpstarter.core.datastore.di.dataStoreModule")

        val starterJson = Json.decodeFromString<StarterJson>(generated.read("starter.json"))
        assertEquals("com.example.notes", starterJson.packageName)
        assertEquals("0.6.0", starterJson.starterVersion)
        assertEquals(ProjectMode.LIB, starterJson.mode)

        val toml = generated.read("gradle/libs.versions.toml")
        assertContains(toml, """starter="0.6.0"""")

        val iosConfig = generated.read("iosApp/AppConfig.xcconfig")
        assertContains(iosConfig, "PRODUCT_BUNDLE_IDENTIFIER=com.example.notes.nativeapp")
        assertFalse("com.kmpstarter.nativeapp" in iosConfig)

        val playJson = generated.read("publish/play.json")
        assertContains(playJson, """"packageName": "com.example.notes.app"""")
        assertFalse("com.kmpstarter" in playJson)

        val googleServices = generated.read("androidApp/google-services.json")
        assertContains(googleServices, """"package_name": "com.example.notes"""")
        assertContains(googleServices, """"bundle_id": "com.example.notes"""")
        assertFalse("com.kmpstarter" in googleServices)

        val leftoverGeneration = Files.exists(generated.cwd.resolve(".starter")) &&
            Files.walk(generated.cwd.resolve(".starter")).use { paths ->
                paths.anyMatch { it.fileName.toString().startsWith("generation") && Files.isDirectory(it) }
            }
        assertFalse(leftoverGeneration)
    }

    @Test
    fun failedGenerateLeavesNoGenerationDir() = runBlocking {
        val root = Files.createTempDirectory("starter-fail")
        val cwd = Files.createDirectories(root.resolve("cwd"))
        val fileManager = CwdFileManager(cwd.toString())
        val repo = StarterProjectsRepositoryImpl(
            fileManager = fileManager,
            sourceCodeProvider = FixedSourceCodeProvider(ByteArray(0)),
        )
        val result = repo.generate(
            StarterProject(
                projectName = "Broken",
                packageName = "com.example.broken",
                mode = ProjectMode.LIB,
                featureName = "notes",
                includeWorkflows = false,
                modules = requiredPlus(),
            ),
        )
        assertTrue(result.isFailure)

        val leftoverGeneration = Files.exists(cwd.resolve(".starter")) &&
            Files.walk(cwd.resolve(".starter")).use { paths ->
                paths.anyMatch { it.fileName.toString().startsWith("generation") && Files.isDirectory(it) }
            }
        assertFalse(leftoverGeneration)
    }

    @Test
    fun generateUsesCustomSourceZipPath() = runBlocking {
        val root = Files.createTempDirectory("starter-zip")
        val template = root.resolve("template")
        MiniStarterTemplate.write(template)
        val cwd = Files.createDirectories(root.resolve("cwd"))
        val zipBytes = FileManagerImpl().createZip(template.toString()).getOrThrow()
        val zipPath = root.resolve("custom.zip")
        Files.write(zipPath, zipBytes)

        val fileManager = CwdFileManager(cwd.toString())
        val repo = StarterProjectsRepositoryImpl(
            fileManager = fileManager,
            sourceCodeProvider = FixedSourceCodeProvider("not-a-zip".toByteArray()),
        )
        val bytes = repo.generate(
            StarterProject(
                projectName = "Zipped",
                packageName = "com.example.zipped",
                mode = ProjectMode.LIB,
                featureName = "notes",
                includeWorkflows = false,
                modules = requiredPlus(),
            ),
            sourceZipPath = zipPath.toString(),
        ).getOrThrow()

        val outZip = root.resolve("project.zip")
        Files.write(outZip, bytes)
        val out = root.resolve("out")
        fileManager.extractZip(outZip.toString(), out.toString()).getOrThrow()
        assertContains(out.resolve("settings.gradle.kts").readText(), """rootProject.name = "Zipped"""")
    }

    @Test
    fun skippingWorkflowsRemovesGithubWorkflowsAndPublish() = runBlocking {
        val generated = generate(
            StarterProject(
                projectName = "Bare",
                packageName = "com.kmpstarter",
                mode = ProjectMode.LIB,
                featureName = "notes",
                includeWorkflows = false,
                modules = requiredPlus(),
            ),
        )

        assertFalse(generated.exists(".github/workflows/ci.yml"))
        assertFalse(generated.exists("publish/readme.txt"))
    }

    @Test
    fun omittingRemoteConfigRemovesGoogleServicesAndInitCall() = runBlocking {
        val generated = generate(
            StarterProject(
                projectName = "NoRemote",
                packageName = "com.kmpstarter",
                mode = ProjectMode.LIB,
                featureName = "notes",
                includeWorkflows = true,
                modules = requiredPlus(),
            ),
        )

        val androidApp = generated.read("androidApp/build.gradle.kts")
        assertFalse("google.services" in androidApp)

        val initKmp = generated.read(
            "composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/InitKmpApp.kt",
        )
        assertFalse("initRemoteConfig" in initKmp)
    }

    @Test
    fun moduleModeKeepsStarterSourcesAndDropsMavenStarterCatalog() = runBlocking {
        val generated = generate(
            StarterProject(
                projectName = "ModularNotes",
                packageName = "com.example.notes",
                mode = ProjectMode.MODULE,
                featureName = "notes",
                includeWorkflows = true,
                modules = requiredPlus(StarterModules.Starter.Ui.Layouts),
            ),
        )

        assertTrue(generated.exists("starter/core/build.gradle.kts"))
        assertTrue(generated.exists("starter/ui/layouts/build.gradle.kts"))
        assertFalse(generated.exists("features/analytics/data/build.gradle.kts"))

        val settings = generated.read("settings.gradle.kts")
        assertContains(settings, """include(":starter:ui:layouts")""")
        assertFalse("""include(":features:analytics:data")""" in settings)

        val composeApp = generated.read("composeApp/build.gradle.kts")
        assertContains(composeApp, "implementation(projects.starter.ui.layouts)")
        assertFalse("projects.features.analytics.data" in composeApp)

        val toml = generated.read("gradle/libs.versions.toml")
        assertFalse(Regex("""(?m)^starter\s*=""").containsMatchIn(toml))
        assertFalse("STARTER LIBRARIES" in toml)

        val initKoin = generated.read(
            "composeApp/src/commonMain/kotlin/com/example/notes/core/di/InitKoin.kt",
        )
        assertFalse("analyticsDataModule" in initKoin)
        assertContains(initKoin, "featureNotesDataModule")
    }

    @Test
    fun hyphenatedFeatureNameRenamesModulePaths() = runBlocking {
        val generated = generate(
            StarterProject(
                projectName = "MyNotes",
                packageName = "com.example.mynotes",
                mode = ProjectMode.LIB,
                featureName = "my-notes",
                includeWorkflows = false,
                modules = requiredPlus(),
            ),
        )

        val settings = generated.read("settings.gradle.kts")
        assertContains(settings, """include(":features:my-notes:data")""")
        assertTrue(generated.exists("features/my-notes/data/build.gradle.kts"))

        val composeApp = generated.read("composeApp/build.gradle.kts")
        assertContains(composeApp, "implementation(projects.features.myNotes.data)")
        assertFalse("projects.features.my-notes" in composeApp)
        assertFalse("starterLibsLocal" in settings)
    }

    private fun requiredPlus(vararg extra: StarterModules): List<StarterModules> = listOf(
        StarterModules.Starter.Core,
        StarterModules.Starter.Utils,
        StarterModules.Starter.Native.Bindings,
        StarterModules.Starter.Ui.Utils,
        StarterModules.Starter.Ui.Components,
        StarterModules.Starter.Ui.Layouts,
        StarterModules.Features.Core.Data,
        StarterModules.Features.Core.Domain,
        StarterModules.Features.Core.Presentation,
        StarterModules.Features.Resources,
        StarterModules.Features.Navigation,
        StarterModules.Features.Locale,
        *extra,
    )

    private suspend fun generate(project: StarterProject): GeneratedProject {
        val root = Files.createTempDirectory("starter-generate")
        val template = root.resolve("template")
        MiniStarterTemplate.write(template)
        val cwd = Files.createDirectories(root.resolve("cwd"))
        val zip = FileManagerImpl().createZip(template.toString()).getOrThrow()
        val fileManager = CwdFileManager(cwd.toString())
        val repo = StarterProjectsRepositoryImpl(
            fileManager = fileManager,
            sourceCodeProvider = FixedSourceCodeProvider(zip),
        )
        val bytes = repo.generate(project).getOrThrow()
        val outZip = root.resolve("project.zip")
        Files.write(outZip, bytes)
        val out = root.resolve("out")
        fileManager.extractZip(outZip.toString(), out.toString()).getOrThrow()
        return GeneratedProject(cwd = cwd, out = out)
    }

    private class GeneratedProject(
        val cwd: Path,
        private val out: Path,
    ) {
        fun read(relative: String): String = out.resolve(relative).readText()
        fun exists(relative: String): Boolean = out.resolve(relative).exists()
    }
}
