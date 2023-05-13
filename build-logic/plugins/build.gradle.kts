/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
}

group = "com.cmgapps.gradle.curriculumvitae.buildlogic.plugins"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        register("ktlint") {
            id = "ktlint"
            implementationClass = "KtlintPlugin"
        }
    }
}

dependencies {
    implementation(libs.squareup.kotlinpoet)
}
