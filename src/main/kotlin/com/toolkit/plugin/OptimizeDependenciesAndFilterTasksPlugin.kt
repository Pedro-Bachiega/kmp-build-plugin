@file:Suppress("UnstableApiUsage")

package com.toolkit.plugin

import com.toolkit.plugin.util.allDefinedDependencies
import com.toolkit.plugin.util.applicationComponent
import com.toolkit.plugin.util.libraryComponent
import com.toolkit.plugin.util.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class OptimizeDependenciesAndFilterTasksPlugin : Plugin<Project> {

    @Suppress("SpreadOperator")
    override fun apply(target: Project) {
        val libraries = target.libs
        val allDefinedLibraries = libraries.allDefinedDependencies
        target.configurations.configureEach {
            resolutionStrategy {
                failOnVersionConflict()
                preferProjectModules()

                setForcedModules(*allDefinedLibraries.toTypedArray())
            }
        }
        val component = kotlin.runCatching { target.libraryComponent }.getOrNull()
            ?: kotlin.runCatching { target.applicationComponent }.getOrNull()
        component?.finalizeDsl {
            target.tasks.configureEach {
                val isRelease = name.contains("release", true)
                val isLint = name.contains("lint", true)
                val isTest = name.contains("test", true)
                val isKover = name.contains("kover", true)
                val mustDisable = isLint || isTest || isKover
                if (isRelease && mustDisable) {
                    enabled = false
                    group = "z-disabled"
                }
            }
        }
    }
}
