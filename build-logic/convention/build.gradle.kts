/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

plugins {
    `kotlin-dsl`
    kotlin("plugin.serialization") version embeddedKotlinVersion
}

group = "com.cmgapps.gradle.curriculumvitae.buildlogic.conventions"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

gradlePlugin {
    plugins {
        register("androidApplicationConvention") {
            id = "curriculumvitae.android.application"
            implementationClass = "AndroidApplicationConventionPlugin"
        }
        register("androidTestConvention") {
            id = "curriculumvitae.android.test"
            implementationClass = "AndroidTestConventionPlugin"
        }
        register("multiplatformAndroidLibraryConvention") {
            id = "curriculumvitae.multiplatform.android.library"
            implementationClass = "MultiplatformAndroidLibraryPlugin"
        }
        register("multiplatformJvmConvention") {
            id = "curriculumVitae.multiplatform.jvm"
            implementationClass = "MultiplatformJvmPlugin"
        }
        register("testConvention") {
            id = "curriculumvitae.test"
            implementationClass = "TestConventionPlugin"
        }
    }
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
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.squareup.kotlinpoet)
    compileOnly(libs.squareup.okio)
    compileOnly(libs.kotlinx.serialization.json)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.hamcrest)
}
