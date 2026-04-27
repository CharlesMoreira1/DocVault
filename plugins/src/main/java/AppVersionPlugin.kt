@file:Suppress("UNUSED")

import AppConfig.COMPILE_SDK
import AppConfig.MIN_SDK
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AppVersionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("com.android.library")

            extensions.configure<LibraryExtension> {
                compileSdk = COMPILE_SDK

                defaultConfig {
                    minSdk = MIN_SDK
                }

                buildTypes {
                    release {
                        isMinifyEnabled = true
                        proguardFiles("proguard-android.txt", "proguard-rules.pro")
                        consumerProguardFiles("proguard-rules.pro")
                    }
                }

                buildFeatures {
                    buildConfig = true
                }
            }
        }
    }
}

object AppConfig {
    const val APPLICATION_NAME_ID = "com.yape.docvault"
    const val VERSION_CODE = 1
    const val VERSION_NAME = "1.0.0"
    const val COMPILE_SDK = 36
    const val TARGET_SDK = 36
    const val MIN_SDK = 28
    val javaCompileVersion = JavaVersion.VERSION_11
}