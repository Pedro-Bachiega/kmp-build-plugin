package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.artifactPrefixProperty
import com.toolkit.plugin.util.attachAllTasksIntoAssembleRelease
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAll
import com.toolkit.plugin.util.vanniktechPublishing
import com.toolkit.plugin.util.versionName
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinMultiplatform
import org.gradle.api.Plugin
import org.gradle.api.Project

internal class PublishPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(target) {
        requireAll(
            currentPluginName = "plugin-multiplatform-publish",
            "plugin-multiplatform-library",
        )
        plugins.apply(libs.findPlugin("vanniktech-publish").get().get().pluginId)

        version = versionName
        publishing.repositories { mavenLocal() }

        // Setup publishing
        with(vanniktechPublishing) {
            coordinates(
                artifactId = "${artifactPrefixProperty?.let { "$it-" }.orEmpty()}${target.name}"
            )

            configure(
                KotlinMultiplatform(
                    javadocJar = JavadocJar.Empty(),
                    sourcesJar = true,
                    androidVariantsToPublish = listOf("release")
                )
            )
        }

        // Attach all needed tasks into assembleRelease task
        attachAllTasksIntoAssembleRelease()
    }
}
