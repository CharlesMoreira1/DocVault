@file:Suppress("UNUSED")

import com.android.build.api.dsl.LibraryExtension
import extension.configureAndroidCompose
import extension.getLibrary
import extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.configure

class AndroidLibraryComposePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            extensions.configure<LibraryExtension> {
                configureAndroidCompose(this)
            }

            dependencies {
                add("implementation", platform(libs.getLibrary("androidx-compose-bom")))
                add("implementation", libs.getLibrary("androidx-activity-compose"))
                add("implementation", libs.getLibrary("androidx-compose-material3"))
                add("implementation", libs.getLibrary("androidx-compose-material-icons-extended"))
                add("implementation", libs.getLibrary("androidx-compose-ui"))
                add("implementation", libs.getLibrary("androidx-compose-ui-graphics"))
                add("implementation", libs.getLibrary("androidx-compose-ui-tooling-preview"))
                add("implementation", libs.getLibrary("lifecycle-viewmodel-compose"))
                add("implementation", libs.getLibrary("lifecycle-runtime-compose"))
                add("implementation", libs.getLibrary("navigation-compose"))
                add("debugImplementation", libs.getLibrary("androidx-compose-ui-tooling"))
                add("debugImplementation", libs.getLibrary("androidx-compose-ui-test-manifest"))
            }
        }
    }
}
