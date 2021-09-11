/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.GenerateBuildConfig
import java.time.LocalDate
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlinx.serialization)
    id("com.squareup.sqldelight")
    ktlint
}

val generatedFilesDir: Provider<Directory> = project.layout.buildDirectory.dir("generated")

val generateBuildConfig by tasks.registering(GenerateBuildConfig::class) {
    outputDir.set(generatedFilesDir)

    val baseUrl by configProperties()
    val debugBaseUrls by configProperties()

    this.baseUrl.set(baseUrl)
    this.debugBaseUrls.set(debugBaseUrls)
    this.buildYear.set(LocalDate.now().year.toString())
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    android()

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        named("commonMain") {
            this.kotlin.srcDir(generatedFilesDir)
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.bundles.ktor.common)
                implementation(libs.koin.core)
                implementation(libs.sqldelight.coroutines)
            }
        }

        named("commonTest") {
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

        all {
            languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }

    targets.all {
        compilations.all {

            compileKotlinTaskProvider {
                dependsOn(generateBuildConfig)
            }
        }
    }
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = "31.0.0"

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion

        consumerProguardFile(projectDir.resolve("proguard-rules.pro"))
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            manifest.srcFile("src/androidMain/AndroidManifest.xml")
        }
    }
}

@OptIn(ExperimentalPathApi::class)
sqldelight {
    database("CvDatabase") {
        packageName = "com.cmgapps.common.curriculumvitae.data.db"
        schemaOutputDirectory =
            file(projectDir.toPath() / "src" / "commonMain" / "sqldelight" / "databases")
        verifyMigrations = true
    }
}
