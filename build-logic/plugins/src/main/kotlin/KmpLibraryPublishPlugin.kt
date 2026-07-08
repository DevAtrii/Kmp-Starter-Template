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

import com.vanniktech.maven.publish.MavenPublishBaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType

/**
 * Publishes KMP library modules (starter + selected features) to Maven Central / `.starter-libs`.
 *
 * Do not set [Project.group] / [Project.version] on the target project — that breaks KMP + Compose
 * Android compilation while [MavenPublishBaseExtension.coordinates] still publishes correctly.
 */
class KmpLibraryPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val path = target.path
        require(path in PUBLISHABLE_MODULE_PATHS) {
            "Module $path is not configured for Maven publishing. Add it to PUBLISHABLE_MODULE_PATHS."
        }

        val libs = target.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val major = libs.findVersion("lib-version-major").get().requiredVersion
        val minor = libs.findVersion("lib-version-minor").get().requiredVersion
        val patch = libs.findVersion("lib-version-patch").get().requiredVersion
        val libVersion = "$major.$minor.$patch"
        val groupId = "io.github.devatrii"
        val artifactId = mavenArtifactId(path)

//        target.group = groupId
//        target.version = libVersion

        target.plugins.apply("com.vanniktech.maven.publish")

        val publishToCentral =
            target.mavenCentralCredentialsPresent() && target.signingCredentialsPresent()

        target.extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            coordinates(groupId, artifactId, libVersion)
            pom {
                name.set(artifactId)
                description.set(
                    "KMP Starter library ($artifactId). See https://github.com/DevAtrii/Kmp-Starter-Template",
                )
                inceptionYear.set("2025")
                url.set("https://github.com/DevAtrii/Kmp-Starter-Template")
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("DevAtrii")
                        name.set("Athar Zaman")
                        url.set("https://github.com/DevAtrii/")
                    }
                }
                scm {
                    url.set("https://github.com/DevAtrii/Kmp-Starter-Template/")
                    connection.set("scm:git:git://github.com/DevAtrii/Kmp-Starter-Template.git")
                    developerConnection.set("scm:git:ssh://git@github.com/DevAtrii/Kmp-Starter-Template.git")
                }
            }
            if (publishToCentral) {
                publishToMavenCentral(automaticRelease = true)
                signAllPublications()
            }
        }

        target.extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "starterLocal"
                    url = target.rootProject.layout.projectDirectory.dir(".starter-libs").asFile.toURI()
                }
            }
        }
    }

    companion object {
        val PUBLISHABLE_MODULE_PATHS: Set<String> =
            setOf(
                // starter
                ":starter:core",
                ":starter:utils",
                ":starter:native:bindings",
                ":starter:ui:utils",
                ":starter:ui:components",
                ":starter:ui:layouts",
                // navigation
                ":features:navigation",
                // analytics
                ":features:analytics:data",
                ":features:analytics:domain",
                // core (app layer — onboarding, splash, shared domain/data)
                ":features:core_app:data",
                ":features:core_app:domain",
                ":features:core_app:presentation",
                // locale
                ":features:locale",
                // remote config
                ":features:remote_config:data",
                ":features:remote_config:domain",
                ":features:remote_config:presentation",
                // purchases
                ":features:purchases:data",
                ":features:purchases:domain",
                ":features:purchases:presentation",
                // notifications
                ":features:notifications:core",
                ":features:notifications:local",
                ":features:notifications:push",
            )

        fun mavenArtifactId(path: String): String =
            when (path) {
                // starter
                ":starter:core" -> "starter-core"
                ":starter:utils" -> "starter-utils"
                ":starter:native:bindings" -> "starter-native-bindings"
                ":starter:ui:utils" -> "starter-ui-utils"
                ":starter:ui:components" -> "starter-ui-components"
                ":starter:ui:layouts" -> "starter-ui-layouts"
                // navigation
                ":features:navigation" -> "feature-navigation"
                // analytics
                ":features:analytics:data" -> "feature-analytics-data"
                ":features:analytics:domain" -> "feature-analytics-domain"
                // core
                ":features:core_app:data" -> "feature-core-data"
                ":features:core_app:domain" -> "feature-core-domain"
                ":features:core_app:presentation" -> "feature-core-presentation"
                // locale
                ":features:locale" -> "feature-locale"
                // remote config
                ":features:remote_config:data" -> "feature-remote-config-data"
                ":features:remote_config:domain" -> "feature-remote-config-domain"
                ":features:remote_config:presentation" -> "feature-remote-config-presentation"
                // purchases
                ":features:purchases:data" -> "feature-purchases-data"
                ":features:purchases:domain" -> "feature-purchases-domain"
                ":features:purchases:presentation" -> "feature-purchases-presentation"
                // notifications
                ":features:notifications:core" -> "feature-notifications-core"
                ":features:notifications:local" -> "feature-notifications-local"
                ":features:notifications:push" -> "feature-notifications-push"
                else -> error("No Maven artifactId configured for project path: $path")
            }
    }

    private fun Project.mavenCentralCredentialsPresent(): Boolean {
        val u = rootProject.findProperty("mavenCentralUsername") as? String
        val p = rootProject.findProperty("mavenCentralPassword") as? String
        return !u.isNullOrBlank() && !p.isNullOrBlank()
    }

    private fun Project.signingCredentialsPresent(): Boolean {
        val inMemory = rootProject.findProperty("signingInMemoryKey") as? String
        if (!inMemory.isNullOrBlank()) return true
        val ring = rootProject.findProperty("signing.secretKeyRingFile") as? String
        return !ring.isNullOrBlank()
    }
}
