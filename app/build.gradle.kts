plugins {
    id("yape.android.application.plugin")
}

android {
    namespace = AppConfig.APPLICATION_NAME_ID
    compileSdk = AppConfig.COMPILE_SDK

    defaultConfig {
        applicationId = AppConfig.APPLICATION_NAME_ID

        versionCode = AppConfig.VERSION_CODE
        versionName = AppConfig.VERSION_NAME

        minSdk = AppConfig.MIN_SDK
        targetSdk = AppConfig.TARGET_SDK
        multiDexEnabled = true
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = AppConfig.javaCompileVersion
        targetCompatibility = AppConfig.javaCompileVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles("proguard-android.txt", "proguard-rules.pro")
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:home:home_presentation"))
    implementation(project(":feature:detail:detail_presentation"))
    implementation(project(":feature:document:document_data"))
    implementation(project(":feature:document:document_domain"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.navigation.compose)
    implementation(libs.timber)
}