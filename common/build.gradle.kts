/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.cmgapps.gradle.curriculumvitae.configProperty
import com.cmgapps.gradle.curriculumvitae.javaLanguageVersion
import java.time.LocalDate
import kotlin.io.path.div

plugins {
    id("curriculumvitae.multiplatform.android.library")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.serialization)
    id("com.squareup.sqldelight")
    id("ktlint")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.licenses)
    id("curriculumvitae.buildconfig")
}

kotlin {
    js(IR) {
        useCommonJs()
        browser()
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach {
        it.binaries.framework {
            baseName = "common"
        }
    }

    sourceSets {
        val commonMain by getting {
            this.kotlin.srcDir(tasks.generateBuildConfig)
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.bundles.ktor.common)
                implementation(libs.koin.core)
                implementation(libs.sqldelight.coroutines)
                implementation(libs.kotlinx.coroutines.core.nativeMt)
                api(libs.kermit)
                implementation(libs.ktor.client.logging)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(libs.ktor.client.mock)
                implementation(libs.turbine)
            }
        }

        named("jvmMain") {
            dependencies {
                implementation(libs.sqldelight.driver.jvm)
            }
        }

        named("jsMain") {
            dependencies {
                implementation(libs.sqldelight.driver.js)
            }
        }

        named("androidMain") {
            dependencies {
                implementation(libs.sqldelight.driver.android)
                implementation(libs.koin.android)
            }
        }

        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        create("iosMain") {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
            dependencies {
                implementation(libs.sqldelight.driver.native)
                implementation(libs.ktor.client.ios)
            }
        }

        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        create("iosTest") {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
            dependencies {
                implementation(libs.ktor.client.mock)
                implementation(libs.kotlinx.coroutines.core.nativeMt)
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }

    targets.all {
        compilations.all {
            compileTaskProvider {
                dependsOn(tasks.generateBuildConfig)
            }
        }
    }

    jvmToolchain {
        languageVersion.set(javaLanguageVersion)
    }
}

android {
    namespace = "com.cmgapps.common.curriculumvitae"
    defaultConfig {
        consumerProguardFile(projectDir.toPath() / "proguard-rules.pro")
    }
}

sqldelight {
    database("CvDatabase") {
        packageName = "com.cmgapps.common.curriculumvitae.data.db"
        schemaOutputDirectory =
            file(projectDir.toPath() / "src" / "commonMain" / "sqldelight" / "databases")
        verifyMigrations = true
    }
}

licenses {
    reports {
        html.enabled = false
        text.enabled = true
    }
}

buildConfig {
    val baseUrl by configProperty
    val debugBaseUrls by configProperty
    val githubReposUrl by configProperty

    this.baseUrl.set(baseUrl)
    this.debugBaseUrls.set(debugBaseUrls)
    this.buildYear.set(LocalDate.now().year.toString())
    this.githubReposUrl.set(githubReposUrl)
}
