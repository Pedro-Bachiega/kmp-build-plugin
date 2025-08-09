package com.toolkit.plugin.gradle

import com.toolkit.plugin.util.applyPlugins
import com.toolkit.plugin.util.artifactPrefixProperty
import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.vanniktechPublishing
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get

internal class PublishPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        applyPlugins("java-gradle-plugin")

        plugins.apply(libs.findPlugin("vanniktech-publish").get().get().pluginId)

        with(vanniktechPublishing) {
            coordinates(
                artifactId = "${artifactPrefixProperty?.let { "$it-" }.orEmpty()}${target.name}",
            )
            publishToMavenCentral()
        }

        with(publishing) {
            repositories { createLocalPathRepository(target) }
            publications {
                create<MavenPublication>("default") {
                    from(components["java"])
                }
            }
        }
    }
}
