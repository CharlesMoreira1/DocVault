@file:Suppress("UNUSED")

import extension.getLibrary
import extension.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidKoinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            dependencies {
                add("implementation", libs.getLibrary("koin-android"))
                add("implementation", libs.getLibrary("koin-compose"))
            }
        }
    }
}
