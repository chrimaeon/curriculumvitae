/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    id("curriculumvitae.android.library")
}

android {
    namespace = "com.cmgapps.android.sharedtest.curriculumvitae"

    buildTypes {
        register("benchmark") {
            initWith(buildTypes["release"])
        }
    }
}

dependencies {
    implementation(projects.app)
    implementation(projects.common)
    implementation(libs.kotlinx.datetime)
}
