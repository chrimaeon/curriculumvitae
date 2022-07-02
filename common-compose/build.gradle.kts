/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

plugins {
    id("curriculumvitae.multiplatform.android.library")
    alias(libs.plugins.jetbrainsCompose)
    id("ktlint")
}

kotlin {
    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(projects.common)
                implementation(compose.runtime)
                implementation(compose.foundation)
                implementation(compose.material)
                implementation(libs.kotlinx.datetime)
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
