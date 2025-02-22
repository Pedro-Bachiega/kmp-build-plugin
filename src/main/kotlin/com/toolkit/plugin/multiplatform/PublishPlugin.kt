package com.toolkit.plugin.multiplatform

import com.toolkit.plugin.util.attachAllTasksIntoAssembleRelease
import com.toolkit.plugin.util.configurePom
import com.toolkit.plugin.util.createLocalPathRepository
import com.toolkit.plugin.util.createSonatypeRepository
import com.toolkit.plugin.util.kotlinMultiplatform
import com.toolkit.plugin.util.publishing
import com.toolkit.plugin.util.requireAll
import com.toolkit.plugin.util.setupJavadocAndSources
import com.toolkit.plugin.util.setupSigning
import com.toolkit.plugin.util.versionName
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.maven.MavenPublication
import org.jetbrains.kotlin.konan.file.File

internal class PublishPlugin : Plugin<Project> {

    private val Project.javadoc: String?
        get() = "$projectDir/build/libs/$name-release-javadoc.jar".takeIf { File(it).exists }

    override fun apply(target: Project) {
        target.requireAll(
            "plugin-multiplatform-publish",
            "plugin-multiplatform-library",
        )
        target.plugins.apply("maven-publish")

        target.kotlinMultiplatform?.run {
            withSourcesJar(true)
            androidTarget().publishLibraryVariants("release")
        } ?: return

        // Setup Javadoc and sources artifacts
        target.setupJavadocAndSources()

        // Setup Publishing
        with(target.publishing) {
            repositories {
                createLocalPathRepository(target)
                createSonatypeRepository(target)
            }

            publications {
                withType(MavenPublication::class.java) {
                    val suffix = when {
                        name.contains("android") -> "-android"
                        name.contains("jvm") -> "-jvm"
                        else -> ""
                    }
                    groupId = target.properties["GROUP_ID"] as String
                    artifactId = "mock-engine-${target.name}$suffix"
                    version = target.versionName

                    target.javadoc?.let { file ->
                        artifact(file) {
                            classifier = "javadoc"
                            extension = "jar"
                        }
                    }
                    pom { target.configurePom(this, false) }
                }
            }
        }

        // Attach all needed tasks into assembleRelease task
        target.attachAllTasksIntoAssembleRelease()

        // Setup Signing
        target.setupSigning()
    }
}
