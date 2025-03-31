import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.serialization)
}

kotlin {
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        moduleName = "composeApp"
        browser {
            val rootDirPath = project.rootDir.path
            val projectDirPath = project.projectDir.path
            commonWebpackConfig {
                outputFileName = "composeApp.js"
                devServer = (devServer ?: KotlinWebpackConfig.DevServer()).apply {
                    static = (static ?: mutableListOf()).apply {
                        // Serve sources to debug inside browser
                        add(rootDirPath)
                        add(projectDirPath)
                    }
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(compose.components.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.lifecycle.compose)
            implementation(libs.serialization.json)
        }
    }
}

tasks.matching { it.name.endsWith("ProcessResources") }.configureEach {
    finalizedBy("patchStringResourceFile")
    finalizedBy("patchResourceCollectors")
}

tasks.register("patchStringResourceFile") {
    doLast {
        val generatedFile = file(
            "build/generated/compose/resourceGenerator/kotlin/commonMainResourceAccessors/gameloopcodmkeymap/composeapp/generated/resources/String0.commonMain.kt"
        )

        if (generatedFile.exists()) {
            var content = generatedFile.readText()
            content = content.replace(
                "org.jetbrains.compose.resources.StringResource", "org.napharcos.gameloopcodmkeymap.theme.res.StringResource"
            )
            content = content.replace(
                "org.jetbrains.compose.resources.ResourceItem", "org.napharcos.gameloopcodmkeymap.theme.res.ResourceItem"
            )
            generatedFile.writeText(content)
        }
    }
}

tasks.register("patchResourceCollectors") {
    doLast {
        val generatedExpectFile = file(
            "build/generated/compose/resourceGenerator/kotlin/commonMainResourceCollectors/gameloopcodmkeymap/composeapp/generated/resources/ExpectResourceCollectors.kt"
        )

        val generatedActualFile = file(
            "build/generated/compose/resourceGenerator/kotlin/wasmJsMainResourceCollectors/gameloopcodmkeymap/composeapp/generated/resources/ActualResourceCollectors.kt"
        )

        if (generatedExpectFile.exists()) {
            var content = generatedExpectFile.readText()
            content = content.replace(
                "org.jetbrains.compose.resources.StringResource", "org.napharcos.gameloopcodmkeymap.theme.res.StringResource"
            )
            generatedExpectFile.writeText(content)
        }

        if (generatedActualFile.exists()) {
            var content = generatedActualFile.readText()
            content = content.replace(
                "org.jetbrains.compose.resources.StringResource", "org.napharcos.gameloopcodmkeymap.theme.res.StringResource"
            )
            generatedActualFile.writeText(content)
        }
    }
}



