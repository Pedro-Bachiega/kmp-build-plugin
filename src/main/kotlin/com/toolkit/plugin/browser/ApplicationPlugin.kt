package com.toolkit.plugin.browser

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.kotlinMultiplatform
import com.toolkit.plugin.util.projectJavaVersionCode
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.kotlinExtension
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

@OptIn(ExperimentalWasmDsl::class)
internal class ApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        error("Browser application not implemented yet")

        applyPlugins(
            "jetbrains-kotlin-multiplatform",
            "jetbrains-compose-compiler",
            "jetbrains-compose-kotlin",
        )

        kotlinExtension.jvmToolchain(projectJavaVersionCode)
        kotlinMultiplatform?.run {
            applyDefaultHierarchyTemplate()
            wasmJs {
                moduleName = "LuliBrowserApp"
                browser {
                    commonWebpackConfig {
                        outputFileName = "luliBrowserApp.js"
                        devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                            static = (static ?: mutableListOf()).apply {
                                add(project.projectDir.path)
                            }
                        }
                    }
                }
                binaries.executable()
            }
        }

        plugins.apply("plugin-lint")
        plugins.apply("plugin-optimize")
    }
}
