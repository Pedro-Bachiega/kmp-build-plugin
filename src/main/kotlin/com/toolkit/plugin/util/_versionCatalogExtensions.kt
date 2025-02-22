package com.toolkit.plugin.util

import org.gradle.api.artifacts.VersionCatalog
import kotlin.jvm.optionals.getOrNull

internal fun VersionCatalog.version(alias: String) =
    findVersion(alias).getOrNull()?.requiredVersion
        ?: error("Unable to find version with alias: $alias")

internal val VersionCatalog.allDefinedDependencies: List<String>
    get() = libraryAliases.map(::findLibrary).mapNotNull { library ->
        library.getOrNull()?.get()?.run {
            versionConstraint.requiredVersion.takeIf { it.isNullOrBlank().not() }
                ?.let { version -> "${module.group}:${module.name}:$version" }
        }
    }.toList()
