package com.toolkit.plugin.multiplatform

import com.android.build.api.dsl.LibraryExtension
import com.toolkit.plugin.commonSetup
import com.toolkit.plugin.util.Config
import com.toolkit.plugin.util.Target
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.projectJavaTarget
import com.toolkit.plugin.util.projectJavaVersionCode
import com.toolkit.plugin.util.version
import kotlinx.serialization.json.Json
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
        target.setupTargets(kotlinMultiplatform ?: return@with)

        plugins.apply("plugin-lint")
        plugins.apply("plugin-optimize")
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

    private fun Project.setupTargets(kotlin: KotlinMultiplatformExtension) = with(kotlin) {
        val configFile = project.file("module-config.json")
        val targets = if (configFile.exists()) {
            Json.decodeFromString<Config>(configFile.readText()).targets
        } else {
            listOf(Target.Android, Target.Jvm) // Default targets
        }

        if (targets.contains(Target.Android)) {
            androidLibrary?.let { setupAndroid(it, kotlin) }
            androidTarget {
                publishLibraryVariants("release")
            }
        }
        if (targets.contains(Target.Jvm)) {
            jvm()
        }
        if (targets.contains(Target.Js)) {
            js(IR) {
                browser()
            }
        }
        if (targets.contains(Target.WasmJs)) {
            wasmJs {
                browser()
            }
        }
        if (targets.contains(Target.WasmWasi)) {
             wasmWasi()
        }
        if (targets.contains(Target.AndroidNativeArm32)) {
            androidNativeArm32()
        }
        if (targets.contains(Target.AndroidNativeArm64)) {
            androidNativeArm64()
        }
        if (targets.contains(Target.AndroidNativeX86)) {
            androidNativeX86()
        }
        if (targets.contains(Target.AndroidNativeX64)) {
            androidNativeX64()
        }
        if (targets.contains(Target.IosArm64)) {
            iosArm64()
        }
        if (targets.contains(Target.IosX64)) {
            iosX64()
        }
        if (targets.contains(Target.IosSimulatorArm64)) {
            iosSimulatorArm64()
        }
        if (targets.contains(Target.WatchosArm32)) {
            watchosArm32()
        }
        if (targets.contains(Target.WatchosArm64)) {
            watchosArm64()
        }
        if (targets.contains(Target.WatchosX64)) {
            watchosX64()
        }
        if (targets.contains(Target.WatchosSimulatorArm64)) {
            watchosSimulatorArm64()
        }
        if (targets.contains(Target.TvosArm64)) {
            tvosArm64()
        }
        if (targets.contains(Target.TvosX64)) {
            tvosX64()
        }
        if (targets.contains(Target.TvosSimulatorArm64)) {
            tvosSimulatorArm64()
        }
        if (targets.contains(Target.MacosX64)) {
            macosX64()
        }
        if (targets.contains(Target.MacosArm64)) {
            macosArm64()
        }
        if (targets.contains(Target.LinuxX64)) {
            linuxX64()
        }
        if (targets.contains(Target.LinuxArm64)) {
            linuxArm64()
        }
        if (targets.contains(Target.MingwX64)) {
            mingwX64()
        }

        applyDefaultHierarchyTemplate()
    }
}
