/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("curriculumvitae.android.library")
    alias(libs.plugins.paparazzi)
}

android {
    // Paparazzi supports only API 31 with plugin version 1.0.0
    compileSdk = 31
    defaultConfig {
        targetSdk = 31
    }
    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    testOptions {
        unitTests.all {
            it.testLogging {
                showStandardStreams = true
            }
        }
    }
}

dependencies {
    testImplementation(projects.app)
    testImplementation(projects.common)
    testImplementation(libs.bundles.androidx)
    testImplementation(libs.kotlinx.datetime)
    testImplementation(libs.compose.foundation)
    testImplementation(libs.compose.material)
    testImplementation(libs.dropbox.store)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.testParameterInjector)
}
