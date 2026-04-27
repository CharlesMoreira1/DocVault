plugins {
    id("yape.app.version.plugin")
    id("yape.android.koin.plugin")
}

android {
    namespace = "com.yape.document_domain"
}

dependencies {
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}