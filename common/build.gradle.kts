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
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.cmgapps.gradle.GenerateBuildConfig
import com.cmgapps.gradle.baseConfig
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import java.time.LocalDate
import kotlin.io.path.div

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.kotlinx.serialization)
    id("com.squareup.sqldelight")
    ktlint
    alias(libs.plugins.licenses)
}

val buildConfigFilesDir: Provider<Directory> =
    project.layout.buildDirectory.dir("generated/buildConfig")

val generateBuildConfig by tasks.registering(GenerateBuildConfig::class) {
    outputDir.set(buildConfigFilesDir)

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
            afterSuite(testCompletionLog())
        }
    }

    android()

    js(IR) {
        useCommonJs()
        browser()
    }

    val iosTarget: (String, KotlinNativeTarget.() -> Unit) -> KotlinNativeTarget = when {
        System.getenv("SDK_NAME")?.startsWith("iphoneos") == true -> ::iosArm64
        System.getenv("SDK_NAME")?.startsWith("iphonesimulator") == true &&
            System.getenv("ARCHS")?.startsWith("arm64") == true -> ::iosSimulatorArm64
        else -> ::iosX64
    }

    iosTarget("ios") {
        binaries {
            framework {
                baseName = "common"
            }
        }
    }

    sourceSets {
        named("commonMain") {
            this.kotlin.srcDir(buildConfigFilesDir)
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

        named("iosMain") {
            dependencies {
                implementation(libs.sqldelight.driver.native)
                implementation(libs.ktor.client.ios)
            }
        }

        named("iosTest") {
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

            compileKotlinTaskProvider {
                dependsOn(generateBuildConfig)
            }
        }
    }
}

android {
    baseConfig(project)

    defaultConfig {
        consumerProguardFile(projectDir.resolve("proguard-rules.pro"))
    }

    sourceSets {
        named("main") {
            manifest.srcFile(projectDir.toPath() / "src" / "androidMain" / "AndroidManifest.xml")
        }
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
