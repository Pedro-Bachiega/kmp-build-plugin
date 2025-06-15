package com.toolkit.plugin.util

import org.gradle.api.Project

internal val Project.groupId: String
    get() = properties["GROUP_ID"] as? String ?: error("Missing GROUP_ID property!")

internal val Project.artifactPrefix: String?
    get() = properties["ARTIFACT_PREFIX"] as? String
