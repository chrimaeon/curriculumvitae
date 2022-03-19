/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(
    kotlin.io.path.ExperimentalPathApi::class,
    org.jetbrains.compose.ExperimentalComposeLibrary::class
)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.LocalDate

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
                implementation(libs.kotlinx.coroutines.swing)
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
            val versionName by versionProperty
            packageVersion = versionName
            vendor = "CMG Mobile Apps"
            copyright = """
            Copyright (c) ${LocalDate.now().year}. Christian Grach <christian.grach@cmgapps.com>

            SPDX-License-Identifier: Apache-2.0
            """.trimIndent()
            licenseFile.set(rootDir.resolve("LICENSE"))

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
