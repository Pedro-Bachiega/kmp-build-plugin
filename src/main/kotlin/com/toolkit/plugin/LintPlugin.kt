package com.toolkit.plugin

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.detekt
import com.toolkit.plugin.util.ktLint
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.projectJavaVersionName
import com.toolkit.plugin.util.version
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.DetektCreateBaselineTask
import io.gitlab.arturbosch.detekt.extensions.DetektReport
import org.gradle.api.Plugin
import org.gradle.api.Project
import java.io.File

internal class LintPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        applyPlugins("lint-detekt", "lint-ktlint")

        // Detekt configuration
        dependencies.add(
            "detektPlugins",
            libs.findLibrary("lint-detekt-formatting").get()
        )

        with(detekt) {
            toolVersion = libs.version("detekt")
            parallel = true
            disableDefaultRuleSets = true
            buildUponDefaultConfig = true
            ignoreFailures = false

            autoCorrect = true
            allRules = false
            source.from("src/main")
            config.setFrom("${target.rootDir}/tools/detekt-config.yml")
            baseline = File("${target.rootDir}/tools/detekt-baseline.xml")
        }

        tasks.withType(Detekt::class.java).configureEach {
            jvmTarget = projectJavaVersionName
            reports.run { listOf(html, xml, txt, sarif, md) }.onEach { it.setup(target) }
        }
        tasks.withType(DetektCreateBaselineTask::class.java).configureEach {
            jvmTarget = projectJavaVersionName
        }

        // KtLint configuration
        ktLint.outputColorName.set("RED")
        ktLint.ignoreFailures.set(false)
    }

    private fun DetektReport.setup(target: Project) {
        required.set(true)
        outputLocation.set(File("${target.projectDir}/build/reports/detekt.${type.extension}"))
    }
}
