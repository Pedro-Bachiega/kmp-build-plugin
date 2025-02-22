package com.toolkit.plugin.multiplatform

import com.android.build.api.dsl.LibraryExtension
import com.toolkit.plugin.commonSetup
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.projectJavaTarget
import com.toolkit.plugin.util.projectJavaVersionCode
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

@OptIn(ExperimentalWasmDsl::class)
internal class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "android-library",
            "jetbrains-kotlin-multiplatform",
            "jetbrains-serialization",
        )

        kotlinExtension.jvmToolchain(projectJavaVersionCode)

        val android = androidLibrary ?: return@with
        val kotlin = kotlinMultiplatform ?: return@with
        setupAndroid(android, kotlin)
        kotlin.jvm()
//        kotlin.wasmJs { browser() }

        plugins.apply("toolkit-lint")
        plugins.apply("toolkit-optimize")
    }

    private fun Project.setupAndroid(
        android: LibraryExtension,
        kotlin: KotlinMultiplatformExtension
    ) = with(android) {
        kotlin.androidTarget().compilations.all {
            compileTaskProvider.configure {
                compilerOptions.jvmTarget.set(projectJavaTarget)
            }
        }

        // Common Setup
        commonSetup()

        compileSdk = libs.version("build-sdk-compile").toInt()
        buildToolsVersion = libs.version("build-tools")

        // Exclusive Library Configurations
        defaultConfig {
            minSdk = libs.version("build-sdk-min-toolkit").toInt()

            consumerProguardFiles("consumer-proguard-rules.pro")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        sourceSets {
            maybeCreate("main").apply {
                java.srcDirs("src/androidMain/kotlin")
                res.srcDirs("src/androidMain/res")
                resources.srcDirs("src/commonMain/resources")
            }
            maybeCreate("test").java.srcDirs("src/test/kotlin")
            maybeCreate("androidTest").java.srcDirs("src/androidTest/kotlin")
            maybeCreate("androidTest").resources.srcDirs("src/androidTest/res")
        }
    }
}
