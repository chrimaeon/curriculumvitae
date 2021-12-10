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

import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div
import buildToolsVersion as depsBuildToolsVersion

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    alias(libs.plugins.jetbrainsCompose)
    ktlint
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

    // js(IR) {
    //     useCommonJs()
    //     browser()
    // }

    sourceSets {
        named("commonMain") {
            dependencies {
                implementation(compose.runtime)
                implementation(compose.foundation)
            }
        }

        all {
            languageSettings.optIn("kotlin.RequiresOptIn")
        }
    }
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = depsBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            @OptIn(ExperimentalPathApi::class)
            manifest.srcFile(projectDir.toPath() / "src" / "androidMain" / "AndroidManifest.xml")
        }
    }
}
