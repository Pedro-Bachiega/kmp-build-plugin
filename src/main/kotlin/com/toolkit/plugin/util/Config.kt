package com.toolkit.plugin.util

import kotlinx.serialization.Serializable

@Serializable
data class Config(
    val targets: List<Target>
)
