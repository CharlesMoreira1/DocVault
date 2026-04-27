package extension

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension,
) {
    pluginManager.apply("org.jetbrains.kotlin.plugin.compose")
    commonExtension.buildFeatures.apply {
        compose = true
    }
}
