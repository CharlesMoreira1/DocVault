plugins {
    `kotlin-dsl`
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.compose.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
}

gradlePlugin {
    plugins {
        register("appVersionPlugin") {
            id = "yape.app.version.plugin"
            implementationClass = "AppVersionPlugin"
        }
        register("androidApplicationPlugin") {
            id = "yape.android.application.plugin"
            implementationClass = "AndroidApplicationPlugin"
        }
        register("androidLibraryPlugin") {
            id = "yape.android.library.plugin"
            implementationClass = "AndroidLibraryPlugin"
        }
        register("androidLibraryComposePlugin") {
            id = "yape.android.library.compose.plugin"
            implementationClass = "AndroidLibraryComposePlugin"
        }
        register("androidKoinPlugin") {
            id = "yape.android.koin.plugin"
            implementationClass = "AndroidKoinPlugin"
        }
    }
}
