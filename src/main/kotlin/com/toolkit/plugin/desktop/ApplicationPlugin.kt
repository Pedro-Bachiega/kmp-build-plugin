package com.toolkit.plugin.desktop

import com.toolkit.plugin.setupKsp
import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension

internal class ApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        applyPlugins(
            "jetbrains-kotlin-multiplatform",
            "jetbrains-compose-compiler",
            "jetbrains-compose-kotlin",
            "google-ksp",
        )

        kotlinExtension.jvmToolchain(projectJavaVersionCode)
        kotlinMultiplatform?.run {
            applyDefaultHierarchyTemplate()
            jvm()
            setupKsp(this)
        }

        plugins.apply("plugin-lint")
        plugins.apply("plugin-optimize")
    }
}
