/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl
import java.time.LocalDate
import java.util.Properties

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatformWasm
    id("ktlint")

    // TODO remove once common can be shared
    alias(libs.plugins.buildConfig)
}

kotlin {
    js(IR) {
        moduleName = "web-canvas"
        browser()
        binaries.executable()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasm {
        moduleName = "web-canvas"
        browser()
        binaries.executable()
    }

    sourceSets {
        val jsWasmMain by creating {
            this.kotlin.srcDir(tasks.generateBuildConfig)
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4-wasm3")
            }
        }

        getByName("wasmMain") {
            dependsOn(jsWasmMain)
        }

        getByName("jsMain") {
            dependsOn(jsWasmMain)
            dependencies {
                // TODO get `:common` working with at least javascript
                // implementation(projects.common)
                implementation(projects.commonCompose)
            }
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider {
                dependsOn(tasks.generateBuildConfig)
            }
        }
    }
}

// TODO remove once common can be shared
buildConfig {
    packageName("com.cmgapps.common.curriculumvitae")
    useKotlinOutput { topLevelConstants = true }

    val configProperties by lazy {
        Properties().apply {
            rootDir.resolve("config.properties").inputStream().use {
                load(it)
            }
        }
    }
    buildConfigField(
        "String",
        "BaseUrl",
        provider {
            """"${configProperties.getProperty("baseUrl")}""""
        },
    )
    buildConfigField(
        "String",
        "DebugBaseUrls",
        provider {
            """"${configProperties.getProperty("debugBaseUrls")}""""
        },
    )
    buildConfigField(
        "String",
        "BuildYear",
        provider {
            """"${LocalDate.now().year}""""
        },
    )
    buildConfigField(
        "String",
        "GithubReposUrl",
        provider {
            """"${configProperties.getProperty("githubReposUrl")}""""
        },
    )
}

compose.experimental {
    web.application {}
}
