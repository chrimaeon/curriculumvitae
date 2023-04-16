/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    kotlin("multiplatform")
    @Suppress("DSL_SCOPE_VIOLATION")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatformWasm
    id("ktlint")
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
            dependencies {
                implementation(compose.runtime)
                implementation(compose.ui)
                implementation(compose.foundation)
                implementation(compose.material)
                @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
                implementation(compose.components.resources)
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
}

compose.experimental {
    web.application {}
}
