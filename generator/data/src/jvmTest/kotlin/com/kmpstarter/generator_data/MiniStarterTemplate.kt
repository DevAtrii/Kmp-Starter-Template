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

import java.nio.file.Files
import java.nio.file.Path

/**
 * Minimal starter template that matches the files [StarterProjectsRepositoryImpl] actually edits.
 * Shape mirrors the real repo so tests assert user-visible generate/include outcomes.
 */
internal object MiniStarterTemplate {

    fun write(root: Path) {
        write(
            root,
            "settings.gradle.kts",
            """
            rootProject.name = "KmpStarter"
            enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    val starterLibsDir = rootDir.resolve(".starter-libs")
                    if (starterLibsDir.exists()) {
                        maven(starterLibsDir.toURI()) {
                            name = "starterLibsLocal"
                            content {
                                includeGroup("io.github.devatrii")
                            }
                        }
                    }
                }
            }

            include(":composeApp")
            include(":starter:core")
            include(":starter:utils")
            include(":starter:native:bindings")
            include(":starter:ui:utils")
            include(":starter:ui:components")
            include(":starter:ui:layouts")
            include(":androidApp")
            include(":features:navigation")
            include(":features:core:domain")
            include(":features:core:data")
            include(":features:core:presentation")
            include(":features:remote_config:domain")
            include(":features:remote_config:data")
            include(":features:remote_config:presentation")
            include(":features:resources")
            include(":features:notifications:core")
            include(":features:notifications:local")
            include(":features:notifications:push")
            include(":features:analytics:domain")
            include(":features:analytics:data")
            include(":features:database")
            include(":features:purchases:data")
            include(":features:purchases:domain")
            include(":features:purchases:presentation")
            include(":features:your-feature:presentation")
            include(":features:your-feature:domain")
            include(":features:your-feature:data")
            include(":features:locale")
            include(":generator:data")
            include(":generator:domain")
            include(":generator:web")
            include(":generator:cli")
            include(":features:analytics:data-firebase")
            """.trimIndent() + "\n",
        )

        write(
            root,
            "composeApp/build.gradle.kts",
            """
            plugins {
                alias(libs.plugins.kotlin.multiplatform)
            }

            kotlin {
                android {
                    namespace = "com.kmpstarterapp"
                }
                sourceSets {
                    commonMain.dependencies {
                        api(projects.starter.core)
                        implementation(projects.features.database)
                        api(projects.starter.ui.utils)
                        implementation(projects.starter.ui.components)
                        implementation(projects.starter.ui.layouts)
                        implementation(projects.features.analytics.data)
                        implementation(projects.features.analytics.domain)
                        implementation(projects.features.purchases.data)
                        implementation(projects.features.purchases.domain)
                        implementation(projects.features.purchases.presentation)
                        implementation(projects.features.notifications.core)
                        implementation(projects.features.notifications.local)
                        implementation(projects.features.notifications.push)
                        implementation(projects.features.navigation)
                        implementation(projects.features.remoteConfig.data)
                        implementation(projects.features.remoteConfig.domain)
                        implementation(projects.features.remoteConfig.presentation)
                        implementation(projects.features.resources)
                        implementation(projects.features.core.data)
                        implementation(projects.features.core.domain)
                        implementation(projects.features.core.presentation)
                        implementation(projects.features.yourFeature.data)
                        implementation(projects.features.yourFeature.domain)
                        implementation(projects.features.yourFeature.presentation)

                        // External Libraries

                    }
                }
            }
            """.trimIndent() + "\n",
        )

        write(
            root,
            "androidApp/build.gradle.kts",
            """
            plugins {
                alias(libs.plugins.android.application)
                alias(libs.plugins.google.services)
            }
            android {
                namespace = "com.kmpstarter.androidapp"
            }
            """.trimIndent() + "\n",
        )

        write(
            root,
            "gradle/libs.versions.toml",
            """
            [versions]
            starter="0.4.0"

            [libraries]
            kotlinx-coroutines-core = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-core", version.ref = "coroutines" }

            # STARTER LIBRARIES
            starter-core = { module = "io.github.devatrii:starter-core", version.ref = "starter" }
            starter-ui-layouts = { module = "io.github.devatrii:starter-ui-layouts", version.ref = "starter" }

            [plugins]
            kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
            """.trimIndent() + "\n",
        )

        write(
            root,
            "composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/di/InitKoin.kt",
            """
            package com.kmpstarterapp.core.di

            import com.kmpstarter.core.datastore.di.dataStoreModule
            import com.kmpstarter.feature_analytics_data.di.analyticsDataModule
            import com.kmpstarter.feature_analytics_domain.di.analyticsDomainModule
            import com.kmpstarter.feature_core_data.di.coreDataModule
            import com.kmpstarter.feature_database.di.databaseModule
            import com.kmpstarter.feature_your_feature_data.di.featureYourDataModule
            import com.kmpstarter.feature_your_feature_domain.di.featureYourDomainModule
            import com.kmpstarter.feature_your_feature_presentation.di.featureYourPresentationModule
            import com.kmpstarter.utils.di.utilsModule

            private val starterModules = module {
                includes(
                    coreDataModule,
                    utilsModule,
                    dataStoreModule,
                    databaseModule,
                    analyticsDomainModule,
                    analyticsDataModule,
                )
            }

            internal fun initKoin() {
                startKoin {
                    modules(
                        starterModules,
                        featureYourDataModule,
                        featureYourDomainModule,
                        featureYourPresentationModule
                    )
                }
            }
            """.trimIndent() + "\n",
        )

        write(
            root,
            "composeApp/src/commonMain/kotlin/com/kmpstarterapp/core/InitKmpApp.kt",
            """
            package com.kmpstarterapp.core

            fun initKmpApp() {
                initKoin()
                initRemoteConfig()
            }

            private fun initRemoteConfig() {
                CoroutineScope(Dispatchers.IO).launch {
                    RemoteConfig.init(
                        minimumFetchInterval = if (platform.debug) 1.seconds else 1.hours
                    )
                }
            }
            """.trimIndent() + "\n",
        )

        writeFeatureSlice(root, "data", "Data")
        writeFeatureSlice(root, "domain", "Domain")
        writeFeatureSlice(root, "presentation", "Presentation")

        write(root, "features/your-feature/data/build.gradle.kts", yourFeatureGradle("data"))
        write(root, "features/your-feature/domain/build.gradle.kts", yourFeatureGradle("domain"))
        write(root, "features/your-feature/presentation/build.gradle.kts", yourFeatureGradle("presentation"))

        dummyModule(root, "starter/core")
        dummyModule(root, "starter/utils")
        dummyModule(root, "starter/native/bindings")
        dummyModule(root, "starter/ui/utils")
        dummyModule(root, "starter/ui/components")
        dummyModule(root, "starter/ui/layouts")
        dummyModule(root, "features/core/data")
        dummyModule(root, "features/core/domain")
        dummyModule(root, "features/core/presentation")
        dummyModule(root, "features/database")
        dummyModule(root, "features/resources")
        dummyModule(root, "features/locale")
        dummyModule(root, "features/navigation")
        dummyModule(root, "features/analytics/data")
        dummyModule(root, "features/analytics/domain")

        write(root, "iosApp/AppConfig.xcconfig", "PRODUCT_BUNDLE_IDENTIFIER=com.kmpstarter.nativeapp\n")
        write(root, "iosApp/Info.plist", "<string>com.kmpstarter.nativeapp</string>\n")
        write(
            root,
            "publish/play.json",
            """
            {
              "packageName": "com.kmpstarter.app"
            }
            """.trimIndent() + "\n",
        )
        write(
            root,
            "androidApp/google-services.json",
            """
            {
              "client": [
                {
                  "android_client_info": {
                    "package_name": "com.kmpstarter"
                  },
                  "android_info": {
                    "package_name": "com.kmpstarter"
                  },
                  "ios_info": {
                    "bundle_id": "com.kmpstarter"
                  }
                }
              ]
            }
            """.trimIndent() + "\n",
        )
        write(root, ".github/workflows/ci.yml", "name: ci\n")
        write(root, ".github/workflows/publish-maven.yml", "name: publish\n")
        write(root, "publish/readme.txt", "publish scripts\n")
        write(root, "docs/getting-started.md", "# docs\n")
        write(root, "generator/cli/readme.md", "# generator\n")
        write(root, "site/index.html", "<html></html>\n")
        write(root, "zensical.toml", "site = true\n")
        write(root, "mkdocs.yml", "site_name: starter\n")
        write(root, ".git/config", "[core]\n")
    }

    private fun writeFeatureSlice(root: Path, slice: String, suffix: String) {
        write(
            root,
            "features/your-feature/$slice/src/commonMain/kotlin/com/kmpstarter/feature_your_feature_$slice/di/FeatureYour${suffix}Module.kt",
            """
            package com.kmpstarter.feature_your_feature_$slice.di

            val featureYour${suffix}Module = module {
            }
            """.trimIndent() + "\n",
        )
    }

    private fun yourFeatureGradle(slice: String): String =
        """
        plugins { alias(libs.plugins.kotlin.multiplatform) }
        kotlin {
            sourceSets {
                commonMain.dependencies {
                    implementation(projects.features.yourFeature.domain)
                }
            }
        }
        """.trimIndent() + "\n"

    private fun dummyModule(root: Path, path: String) {
        write(root, "$path/build.gradle.kts", "plugins { }\n")
        write(
            root,
            "$path/src/commonMain/kotlin/com/kmpstarter/Dummy.kt",
            "package com.kmpstarter\nclass Dummy\n",
        )
    }

    private fun write(root: Path, relative: String, content: String) {
        val file = root.resolve(relative)
        Files.createDirectories(file.parent)
        Files.writeString(file, content)
    }
}
