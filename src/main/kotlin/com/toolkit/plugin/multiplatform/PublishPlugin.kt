package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.artifactPrefix
import com.toolkit.plugin.util.attachAllTasksIntoAssembleRelease
import com.toolkit.plugin.util.configurePom
import com.toolkit.plugin.util.groupId
import com.toolkit.plugin.util.libs
import com.toolkit.plugin.util.requireAll
import com.toolkit.plugin.util.setupJavadocAndSources
import com.toolkit.plugin.util.setupSigning
import com.toolkit.plugin.util.vanniktechPublishing
import com.toolkit.plugin.util.versionName
import com.vanniktech.maven.publish.SonatypeHost
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.konan.file.File

internal class PublishPlugin : Plugin<Project> {

    private val Project.javadoc: String?
        get() = "$projectDir/build/libs/$name-release-javadoc.jar".takeIf { File(it).exists }

    override fun apply(target: Project) = with(target) {
        requireAll(
            currentPluginName = "plugin-multiplatform-publish",
            "plugin-multiplatform-library",
        )
        plugins.apply(target.libs.findPlugin("vanniktech-publish").get().get().pluginId)

        // Setup Javadoc and sources artifacts
        setupJavadocAndSources()

        // Setup publishing
        with(vanniktechPublishing) {
            publishToMavenCentral(host = SonatypeHost.S01, automaticRelease = false)
            signAllPublications()
            coordinates(
                groupId = groupId,
                artifactId = "${artifactPrefix?.let { "$it-" }.orEmpty()}${target.name}",
                version = target.versionName
            )
            pom { configurePom(this, false) }
        }

        // Attach all needed tasks into assembleRelease task
        attachAllTasksIntoAssembleRelease()

        // Setup Signing
        setupSigning()
    }
}
