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

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StarterModulesContractTest {

    @Test
    fun layoutsPullsComponentsUtilsCoreAndBindings() {
        val layouts = StarterModules.Starter.Ui.Layouts
        val deps = collect(layouts).map { it.moduleGradlePath() }

        assertTrue(":starter:ui:components" in deps)
        assertTrue(":starter:ui:utils" in deps)
        assertTrue(":starter:core" in deps)
        assertTrue(":starter:utils" in deps)
        assertTrue(":starter:native:bindings" in deps)
        assertTrue(":starter:ui:layouts" in deps)
    }

    @Test
    fun gradlePathsMatchRepoLayout() {
        assertEquals(":starter:ui:layouts", StarterModules.Starter.Ui.Layouts.moduleGradlePath())
        assertEquals("starter/ui/layouts", StarterModules.Starter.Ui.Layouts.moduleFilePath())
        assertEquals(
            ":features:analytics:data-firebase",
            StarterModules.Features.Analytics.DataFirebase.moduleGradlePath(),
        )
        assertEquals(
            "features/remote_config/data",
            StarterModules.Features.RemoteConfig.Data.moduleFilePath(),
        )
    }

    @Test
    fun libVsModuleGradleAccessors() {
        val layouts = StarterModules.Starter.Ui.Layouts
        assertEquals("projects.starter.ui.layouts", layouts.moduleGradleDep(ProjectMode.MODULE))
        assertEquals("libs.starter.ui.layouts", layouts.moduleGradleDep(ProjectMode.LIB))

        val firebase = StarterModules.Features.Analytics.DataFirebase
        assertEquals(
            "projects.features.analytics.dataFirebase",
            firebase.moduleGradleDep(ProjectMode.MODULE),
        )
        assertEquals(
            "libs.starter.feature.analytics.data.firebase",
            firebase.moduleGradleDep(ProjectMode.LIB),
        )
    }

    @Test
    fun mavenArtifactIdsMatchPublishedCoordinates() {
        assertEquals("starter-ui-layouts", StarterModules.Starter.Ui.Layouts.mavenArtifactId())
        assertEquals("starter-core", StarterModules.Starter.Core.mavenArtifactId())
        assertEquals(
            "feature-analytics-data-firebase",
            StarterModules.Features.Analytics.DataFirebase.mavenArtifactId(),
        )
        assertEquals(
            "feature-remote-config-data",
            StarterModules.Features.RemoteConfig.Data.mavenArtifactId(),
        )
        assertEquals("feature-navigation", StarterModules.Features.Navigation.mavenArtifactId())
    }

    @Test
    fun koinModulesOnCoreDataReturnsCoreDataModule() {
        assertEquals(listOf("coreDataModule"), StarterModules.Features.Core.Data.koinModules())
    }

    @Test
    fun hyphenatedFeatureNamesMatchGradleAndPackages() {
        val project = StarterProject(
            projectName = "Notes",
            packageName = "com.example.notes",
            featureName = "my-notes",
        )
        assertEquals("MyNotes", project.getFeatureNameAsPascalCasing())
        assertEquals("my-notes", project.featureGradleIncludeName())
        assertEquals("myNotes", project.featureGradleAccessorName())
        assertEquals("my_notes", project.featurePackageSegment())
    }

    @Test
    fun featureNamePascalCaseMatchesKoinRename() {
        assertEquals("Notes", StarterProject(projectName = "Notes", packageName = "com.example.notes", featureName = "notes").getFeatureNameAsPascalCasing())
        assertEquals("MyNotes", StarterProject(projectName = "Notes", packageName = "com.example.notes", featureName = "my-notes").getFeatureNameAsPascalCasing())
        assertEquals("MyNotes", StarterProject(projectName = "Notes", packageName = "com.example.notes", featureName = "my_notes").getFeatureNameAsPascalCasing())
        assertEquals("MyNotes", StarterProject(projectName = "Notes", packageName = "com.example.notes", featureName = "my notes").getFeatureNameAsPascalCasing())
        assertEquals("", StarterProject(projectName = "Notes", packageName = "com.example.notes", featureName = null).getFeatureNameAsPascalCasing())
    }

    private fun collect(module: StarterModules): Set<StarterModules> {
        val ordered = linkedSetOf<StarterModules>()
        fun visit(current: StarterModules) {
            current.dependencies().forEach(::visit)
            ordered.add(current)
        }
        visit(module)
        return ordered
    }
}
