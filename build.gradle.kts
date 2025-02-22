plugins {
    id("java-gradle-plugin")
    alias(libs.plugins.jetbrains.kotlin.jvm)
    alias(libs.plugins.jetbrains.kotlin.dsl)

    alias(libs.plugins.lint.detekt)
    alias(libs.plugins.lint.ktlint)
    id(libs.plugins.quality.jacoco.get().pluginId)
}

kotlin { jvmToolchain(17) }
jacoco { toolVersion = libs.versions.jacoco.get() }

group = "com.toolkit.plugin"
version = "1.0.0"

dependencies {
    compileOnly(gradleApi())

    implementation(libs.plugin.androidx.plugin)
    implementation(libs.plugin.lint.detekt)
    implementation(libs.plugin.lint.ktlint)
    implementation(libs.plugin.jetbrains.kotlin.plugin)
    implementation(libs.plugin.jetbrains.extensions)
    implementation(libs.plugin.jetbrains.kover)
}

sourceSets {
    main {
        java { srcDirs("src/main/java") }
        kotlin { srcDirs("src/main/kotlin") }
    }
}

gradlePlugin {
    plugins {
        //region Android
        create("toolkit-android-application") {
            id = "toolkit-android-application"
            displayName = "Android Application Plugin"
            description = "\\o/"
            implementationClass = "com.toolkit.plugin.android.ApplicationPlugin"
        }

        create("toolkit-android-library") {
            id = "toolkit-android-library"
            displayName = "Android Library Plugin"
            description = "\\o/"
            implementationClass = "com.toolkit.plugin.android.LibraryPlugin"
        }
        //endregion

        //region Browser
        create("toolkit-browser-application") {
            id = "toolkit-browser-application"
            displayName = "Browser Application Plugin"
            description = "\\o/"
            implementationClass = "com.toolkit.plugin.browser.ApplicationPlugin"
        }
        //endregion

        //region Desktop
        create("toolkit-desktop-application") {
            id = "toolkit-desktop-application"
            displayName = "Desktop Application Plugin"
            description = "\\o/"
            implementationClass = "com.toolkit.plugin.desktop.ApplicationPlugin"
        }
        //endregion

        //region Multiplatform
        create("toolkit-multiplatform-library") {
            id = "toolkit-multiplatform-library"
            displayName = "Desktop Library Plugin"
            description = "\\o/"
            implementationClass = "com.toolkit.plugin.multiplatform.LibraryPlugin"
        }
        //endregion

        //region Generic
        create("toolkit-compose") {
            id = "toolkit-compose"
            displayName = "Toolkit Compose Plugin"
            description = "Compose Plugin"
            implementationClass = "com.toolkit.plugin.ComposePlugin"
        }
        create("toolkit-optimize") {
            id = "toolkit-optimize"
            displayName = "Toolkit Optimization Plugin"
            description = "Optimize dependencies"
            implementationClass =
                "com.toolkit.plugin.ToolkitOptimizeDependenciesAndFilterTasksPlugin"
        }

        create("toolkit-lint") {
            id = "toolkit-lint"
            displayName = "Toolkit Lint Plugin"
            description = "Enables and configure lint for module"
            implementationClass = "com.toolkit.plugin.ToolkitLintPlugin"
        }

        create("toolkit-test") {
            id = "toolkit-test"
            displayName = "Toolkit Test Plugin"
            description = "Enables and configure test for module"
            implementationClass = "com.toolkit.plugin.ToolkitTestPlugin"
        }
        //endregion
    }
}
