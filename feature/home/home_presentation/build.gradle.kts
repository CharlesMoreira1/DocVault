plugins {
    id("yape.app.version.plugin")
    id("yape.android.koin.plugin")
    id("yape.android.library.plugin")
    id("yape.android.library.compose.plugin")
}

android {
    namespace = "com.yape.home_presentation"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:document:document_domain"))

    implementation(libs.accompanist.permissions)
    implementation(libs.kotlinx.collections.immutable)

    testImplementation(libs.junit)
    testImplementation(libs.mockk)
    testImplementation(libs.turbine)
    testImplementation(libs.kotlinx.coroutines.test)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
