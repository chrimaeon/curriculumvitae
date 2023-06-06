/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

plugins {
    id("curriculumvitae.multiplatform.android.library")
    id("org.jetbrains.compose") version libs.versions.composeMultiplatformWasm
    id("ktlint")
}

kotlin {
    js(IR) {
        browser()
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasm {
        browser()
    }

    @OptIn(ExperimentalComposeLibrary::class)
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(compose.components.resources)
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

android {
    namespace = "com.cmgapps.common.curriculumvitae.compose"
}
