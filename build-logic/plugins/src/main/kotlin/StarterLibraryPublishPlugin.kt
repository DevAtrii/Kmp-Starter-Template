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

class StarterLibraryPublishPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        val libs = target.extensions.getByType<VersionCatalogsExtension>().named("libs")
        val major = libs.findVersion("lib-version-major").get().requiredVersion
        val minor = libs.findVersion("lib-version-minor").get().requiredVersion
        val patch = libs.findVersion("lib-version-patch").get().requiredVersion
        val libVersion = "$major.$minor.$patch"
        val groupId = "io.github.devatrii"
        val artifactId = mavenArtifactId(target.path)

        // Do not assign project.group / project.version here: that breaks KMP + Compose
        // Android compilation (circular task dependency) while coordinates() still publishes correctly.

        target.plugins.apply("com.vanniktech.maven.publish")

        val publishToCentral =
            target.mavenCentralCredentialsPresent() && target.signingCredentialsPresent()

        target.extensions.configure<MavenPublishBaseExtension>("mavenPublishing") {
            coordinates(groupId, artifactId, libVersion)
            pom {
                name.set(artifactId)
                description.set(
                    "KMP Starter shared library ($artifactId). See https://github.com/DevAtrii/Kmp-Starter-Template",
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
                        name.set("Athar Gul")
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

    private fun mavenArtifactId(path: String): String =
        when (path) {
            ":starter:core" -> "starter-core"
            ":starter:utils" -> "starter-utils"
            ":starter:native:bindings" -> "starter-native-bindings"
            ":starter:ui:utils" -> "starter-ui-utils"
            ":starter:ui:components" -> "starter-ui-components"
            ":starter:ui:layouts" -> "starter-ui-layouts"
            else -> error("Unexpected starter library project path: $path")
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
