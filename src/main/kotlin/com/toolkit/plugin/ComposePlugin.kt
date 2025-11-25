package com.toolkit.plugin

import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

internal class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins(
            "jetbrains-compose-compiler",
            "jetbrains-compose-kotlin",
        )

        kotlinMultiplatform?.let { setupDefaultDependencies(it) }
        (androidApplication ?: androidLibrary)?.run { buildFeatures.compose = true }
            ?: return@with
    }

    private fun setupDefaultDependencies(kotlinExtension: KotlinMultiplatformExtension) {
        kotlinExtension.sourceSets.all {
            languageSettings {
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
    }
}
