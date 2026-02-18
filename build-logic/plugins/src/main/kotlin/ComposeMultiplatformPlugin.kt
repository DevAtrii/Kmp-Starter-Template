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
                            implementation(libs.findLibrary("compose-runtime").get())
                            implementation(libs.findLibrary("compose-foundation").get())
                            implementation(libs.findLibrary("compose-material3").get())
                            implementation(libs.findLibrary("compose-ui").get())
                            implementation(libs.findLibrary("compose-components-resources").get())
                            implementation(libs.findLibrary("compose-material-icons-extended").get())
                            implementation(libs.findLibrary("compose-ui-tooling-preview").get())
                        }
                    }
                    androidMain {
                        // Compose UI
                        dependencies {
                            implementation(libs.findLibrary("androidx.activity.compose").get())
                            implementation(libs.findLibrary("compose-ui-tooling").get())
                            implementation(libs.findLibrary("compose-ui-tooling-preview").get())
                        }
                    }
                }
            }
        }
    }
}
