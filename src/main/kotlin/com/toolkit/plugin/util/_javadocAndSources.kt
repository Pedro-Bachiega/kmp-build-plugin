package com.toolkit.plugin.util

import org.gradle.api.Project
import org.gradle.api.tasks.javadoc.Javadoc
import org.gradle.jvm.tasks.Jar
import org.jetbrains.kotlin.konan.file.File

internal fun Project.setupJavadocAndSources() {
    val sourcesJar = setupSources()
    val javadocJar = setupJavadoc()
    setupArtifacts(javadocJar, sourcesJar)

    tasks.whenTaskAdded {
        when (name) {
            "assembleRelease" -> finalizedBy(sourcesJar, javadocJar)
            "signToolkitPublication" -> dependsOn(sourcesJar, javadocJar)
            "signKotlinMultiplatformPublication" -> dependsOn(sourcesJar, javadocJar)
        }
    }
}

private fun Project.setupJavadoc(): Jar {
    configurations.maybeCreate("jacocoDeps")

    val javadoc = tasks.register("javadoc", Javadoc::class.java) {
        val list = ArrayList<java.io.File>()
        androidLibrary2.sourceSets.forEach { set -> list.addAll(set.java.srcDirs) }

        isFailOnError = false
        setExcludes(listOf("**/*.kt", "**/*.java"))
        source(list)
        classpath += files(androidLibrary2.bootClasspath.joinToString(separator = File.separator))
        classpath += configurations.named("jacocoDeps").get()
    }.get()

    return tasks.register("javadocJar", Jar::class.java) {
        dependsOn(javadoc)
        archiveClassifier.set("javadoc")
        from(javadoc.destinationDir)
    }.get()
}

private fun Project.setupSources() = if (tasks.findByName("sourcesJar") == null) {
    tasks.register("sourcesJar", Jar::class.java) {
        val mainSource = androidLibrary2.sourceSets.named("main").get().java.srcDirs
        from(mainSource)
        archiveClassifier.set("sources")
    }.get()
} else {
    tasks.named("sourcesJar", Jar::class.java).get()
}

private fun Project.setupArtifacts(javadoc: Jar, sources: Jar) {
    artifacts {
        add("archives", javadoc)
        add("archives", sources)
    }
}
