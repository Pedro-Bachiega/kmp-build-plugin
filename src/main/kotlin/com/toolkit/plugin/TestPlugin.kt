@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin

import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.jacoco
import com.toolkit.plugin.util.kover
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.version
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class TestPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        plugins.apply("jacoco")
        applyPlugins("jetbrains-kover")

        // Kover configuration
        kover.currentProject {
            createVariant("combined") {
                val flavors = androidApplication?.productFlavors ?: androidLibrary?.productFlavors
                flavors?.forEach { add("${it.name}Debug") } ?: add("debug")

                if (plugins.hasPlugin("plugin-desktop-application") || plugins.hasPlugin("plugin-multiplatform-library")) {
                    add("jvm")
                }
            }
        }

        // Jacoco configuration
        jacoco.toolVersion = target.libs.version("jacoco")

        // Regular Test configuration
        runCatching { setForApplication() }
        runCatching { setForLibrary() }
    }

    private fun Project.setForApplication() = with(androidApplication!!) {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes.maybeCreate("debug").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }
        buildTypes.maybeCreate("release").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }

        testOptions {
            unitTests.all { it.enabled = false }
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
            animationsDisabled = true
        }
    }

    private fun Project.setForLibrary() = with(androidLibrary!!) {
        defaultConfig {
            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        }

        buildTypes.maybeCreate("debug").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }
        buildTypes.maybeCreate("release").apply {
            enableAndroidTestCoverage = false
            enableUnitTestCoverage = false
        }

        testOptions {
            unitTests.isIncludeAndroidResources = true
            unitTests.isReturnDefaultValues = true
            animationsDisabled = true
        }
    }
}
