/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    kotlin("plugin.serialization") version embeddedKotlinVersion
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
        register("obfuscateEmail") {
            id = "obfuscateEmail"
            implementationClass = "ObfuscateEmailPlugin"
        }
        register("generateJniData") {
            id = "generateJniData"
            implementationClass = "GenerateJniDataPlugin"
        }
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    implementation(libs.squareup.kotlinpoet)
    compileOnly(libs.kotlinx.serialization.json)
    compileOnly(libs.squareup.okio)
}
