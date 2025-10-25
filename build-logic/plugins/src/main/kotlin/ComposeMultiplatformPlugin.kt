import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class ComposeMultiplatformPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            with(pluginManager) {
                apply(libs.findPlugin("compose.multiplatform").get().get().pluginId)
                apply(libs.findPlugin("compose.compiler").get().get().pluginId)
            }
            val kotlinMultiplatformExtension = extensions.getByType<KotlinMultiplatformExtension>()
            val compose = extensions.getByType<ComposeExtension>().dependencies
            with(kotlinMultiplatformExtension) {
                with(sourceSets) {
                    commonMain {
                        dependencies {
                            implementation(compose.runtime)
                            implementation(compose.foundation)
                            implementation(compose.material3)
                            implementation(compose.ui)
                            implementation(compose.components.resources)
                            implementation(compose.components.uiToolingPreview)
                            implementation(compose.materialIconsExtended)
                        }
                    }
                    androidMain {
                        // Compose UI
                        dependencies {
                            implementation(libs.findLibrary("androidx.activity.compose").get())
                        }
                    }
                }
            }
        }
    }
}
