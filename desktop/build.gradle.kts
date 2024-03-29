/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(
    kotlin.io.path.ExperimentalPathApi::class,
    org.jetbrains.compose.ExperimentalComposeLibrary::class,
)

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.time.LocalDate

plugins {
    id("curriculumVitae.multiplatform.jvm")
    alias(libs.plugins.jetbrainsCompose)
    id("ktlint")
}

kotlin {
    sourceSets {
        named("jvmMain") {
            dependencies {
                implementation(projects.common)
                implementation(projects.commonCompose)
                implementation(compose.desktop.currentOs)
                implementation(compose.uiTooling)
                implementation(libs.koin.core)
                implementation(libs.koin.compose.mpp)
                implementation(libs.ktor.client.java)
                implementation(libs.kotlinx.datetime)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        named("jvmTest") {
            dependencies {
                implementation(libs.junit.junit)
                runtimeOnly(libs.junit.vintageEngine)
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
        mainClass = "com.cmgapps.desktop.curriculumvitae.MainKt"

        buildTypes {
            release {
                proguard {
                    isEnabled.set(false)
                    configurationFiles.from(projectDir.resolve("proguard-rules.pro"))
                }
            }
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Curriculum Vitae"
            packageVersion = libs.versions.versionName.get()
            vendor = "CMG Mobile Apps"
            copyright =
                "Copyright (c) ${LocalDate.now().year}. Christian Grach <christian.grach@cmgapps.com>"
            licenseFile.set(rootDir.resolve("LICENSE"))

            modules("java.net.http", "java.sql")

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
