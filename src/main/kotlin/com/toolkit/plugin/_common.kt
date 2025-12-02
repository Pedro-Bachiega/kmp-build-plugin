package com.toolkit.plugin

import com.android.build.api.dsl.CommonExtension
import com.toolkit.plugin.util.projectJavaVersion
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension

internal fun CommonExtension<*, *, *, *, *, *>.commonSetup() {
    buildFeatures.buildConfig = true

    androidResources { noCompress.add("") }

    compileOptions {
        sourceCompatibility(projectJavaVersion)
        targetCompatibility(projectJavaVersion)
    }

    packaging {
        resources.excludes.add("META-INF/LICENSE")
        resources.pickFirsts.add("protobuf.meta")
        jniLibs.keepDebugSymbols.addAll(setOf("*/mips/*.so", "*/mips64/*.so"))
    }
}

internal fun Project.setupKsp(kotlinExtension: KotlinProjectExtension) {
    plugins.withId("com.google.devtools.ksp") {
        val kspMetadata = tasks.findByName("kspCommonMainKotlinMetadata")
        val compileMetadata = tasks.findByName("compileCommonMainKotlinMetadata")
        if (kspMetadata != null && compileMetadata != null) {
            compileMetadata.dependsOn(kspMetadata)
        }

        val kspJvm = tasks.findByName("kspKotlinJvm")
        val compileJvm = tasks.findByName("compileKotlinJvm")
        if (compileJvm != null) {
            kspMetadata?.let { compileJvm.dependsOn(it) }
            kspJvm?.let { compileJvm.dependsOn(it) }
        }
    }

    kotlinExtension.sourceSets.named("commonMain") {
        kotlin.srcDir(
            "${layout.buildDirectory.get()}/generated/ksp/metadata/commonMain/kotlin"
        )
    }
}
