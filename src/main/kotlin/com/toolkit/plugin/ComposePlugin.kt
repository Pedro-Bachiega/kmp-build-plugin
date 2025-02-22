package com.toolkit.plugin

import com.android.build.api.dsl.CommonExtension
import com.toolkit.plugin.util.androidApplication
import com.toolkit.plugin.util.androidLibrary
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class ComposePlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        applyPlugins(
            "jetbrains-compose-compiler",
            "jetbrains-compose-kotlin",
        )

        kotlinMultiplatform?.let(::setupDefaultDependencies)
        (androidApplication ?: androidLibrary)?.run { buildFeatures.compose = true }
            ?: return@with
    }
}
