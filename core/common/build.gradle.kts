plugins {
    id("yape.app.version.plugin")
    id("yape.android.library.compose.plugin")
    id("yape.android.koin.plugin")
}

android {
    namespace = "com.yape.common"
}

dependencies {
    implementation(libs.timber)
    implementation(libs.androidx.biometric)
}
