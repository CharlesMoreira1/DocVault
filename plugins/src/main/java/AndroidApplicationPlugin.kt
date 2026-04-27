@file:Suppress("UNUSED")

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.kotlin.dsl.configure
import extension.configureAndroidCompose
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            pluginManager.apply {
                apply("com.android.application")
            }
            extensions.configure<ApplicationExtension> {
                configureAndroidCompose(this)
            }
        }
    }
}
