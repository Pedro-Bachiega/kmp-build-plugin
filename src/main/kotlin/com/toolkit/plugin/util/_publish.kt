package com.toolkit.plugin.util

import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.RepositoryHandler

internal fun RepositoryHandler.createLocalPathRepository(project: Project) = maven {
    name = "LocalPath"
    url = project.uri(project.rootProject.layout.buildDirectory.asFile.get().absolutePath)
}
