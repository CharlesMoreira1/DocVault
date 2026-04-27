plugins {
    alias(libs.plugins.kotlin.serialization)
    id("yape.app.version.plugin")
    id("yape.android.koin.plugin")
}

android {
    namespace = "com.yape.document_data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":feature:document:document_domain"))
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)
}
