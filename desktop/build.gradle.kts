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
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class, org.jetbrains.compose.ExperimentalComposeLibrary::class)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    alias(libs.plugins.jetbrainsCompose)
    ktlint
}

kotlin {
    jvm {
        withJava()
        testRuns["test"].executionTask.configure {
            testLogging {
                events("passed", "skipped", "failed")
            }
            afterSuite(testCompletionLog())
        }
    }

    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(projects.common)
                implementation(projects.commonCompose)
                implementation(compose.desktop.currentOs)
                implementation(libs.koin.core)
                implementation(libs.ktor.client.java)
                implementation(libs.kotlinx.datetime)
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(libs.junit.junit)
                implementation(compose.uiTestJUnit4)
                implementation(libs.koin.test)
                implementation(libs.koin.testJunit)
                implementation(libs.mockito.kotlin)
                implementation(libs.sqldelight.driver.jvm)
                implementation(libs.kotlinx.coroutinesTest)
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Curriculum Vitae"
            packageVersion = "1.0.0"

            windows {
                menuGroup = "Curriculum Vitae"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "f4090642-a6c1-4b76-8e5a-957dd09ca6ee"
            }

            macOS {
                bundleID = "com.cmgapps.mac.curriculumvitae"
                iconFile.set(projectDir.resolve("icons").resolve("MacOS.icns"))
            }
        }
    }
}
