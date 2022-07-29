/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
    // alias(libs.plugins.kotlinx.serialization)
}

group = "com.cmgapps.gradle.curriculumvitae.buildlogic"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }
}

dependencies {
    implementation(libs.android.gradlePlugin)
    implementation(libs.kotlin.gradlePlugin)
    implementation(libs.kotlinx.serialization.json)
    implementation("com.squareup:kotlinpoet:1.11.0")
    implementation("com.squareup.okio:okio:3.1.0")
    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}
