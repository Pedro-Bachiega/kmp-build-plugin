@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin.android

import com.android.build.api.dsl.ApplicationExtension
import com.toolkit.plugin.commonSetup
import com.toolkit.plugin.setupKsp
import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinAndroid
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.projectJavaTarget
import com.toolkit.plugin.util.projectJavaVersionCode
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

internal class ApplicationPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "android-application",
            "jetbrains-kotlin-android",
            "jetbrains-compose-compiler",
            "jetbrains-compose-kotlin",
            "jetbrains-serialization",
        )

        val kotlin = kotlinAndroid ?: return@with
        val android = androidApplication ?: return@with

        kotlin.jvmToolchain(projectJavaVersionCode)
        kotlin.compilerOptions { jvmTarget.set(projectJavaTarget) }

        setup(android, kotlin)
        setupKsp(kotlin)

        plugins.apply("plugin-lint")
        plugins.apply("plugin-test")
        plugins.apply("plugin-optimize")
    }

    private fun Project.setup(
        android: ApplicationExtension,
        kotlin: KotlinAndroidProjectExtension
    ) = with(android) {
        // Exclusive Application Configurations
        compileSdk = libs.version("build-sdk-compile").toInt()
        buildToolsVersion = libs.version("build-tools")

        defaultConfig {
            minSdk = libs.version("build-sdk-min-sample").toInt()
            targetSdk = libs.version("build-sdk-target").toInt()

            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

            versionCode = libs.version("build-version-code").toInt()
            versionName = libs.version("build-version-name")

            androidResources.localeFilters += listOf("en-US", "pt-BR")
        }

        // TODO Config json for buildTypes and flavors
        buildTypes.maybeCreate("release").apply { isMinifyEnabled = false }
        buildTypes.maybeCreate("debug").apply {
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
        }

        // Common Setup
        commonSetup()

        sourceSets {
            maybeCreate("main").java.srcDirs("src/main/kotlin")
            maybeCreate("test").java.srcDirs("src/test/kotlin")
            maybeCreate("androidTest").java.srcDirs("src/androidTest/kotlin")
            maybeCreate("androidTest").resources.srcDirs("src/androidTest/res")
        }
    }
}
