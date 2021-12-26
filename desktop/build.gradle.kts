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

import org.gradle.jvm.tasks.Jar
import org.jetbrains.compose.ExperimentalComposeLibrary
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import proguard.gradle.ProGuardTask
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

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

        @OptIn(ExperimentalComposeLibrary::class)
        named("jvmTest") {
            dependencies {
                implementation(libs.junit.junit)
                implementation(compose.uiTestJUnit4)
                implementation(libs.koin.test)
                implementation(libs.koin.testJunit)
                implementation(libs.mockito.kotlin)
                implementation(libs.sqldelight.driver.jvm)
            }
        }
    }
}

@OptIn(ExperimentalPathApi::class)
val proguard by tasks.registering(ProGuardTask::class) {

    description = "Create a minified version of the uber jar"

    val packageTask =
        tasks.named("packageUberJarForCurrentOS", Jar::class).get()

    injars(packageTask.archiveFile)
    libraryjars(
        fileTree(
            System.getProperty("java.home") + "/jmods"
        ) {
            exclude("**.jar", "module-info.class")
        }
    )

    configuration(projectDir.toPath() / "proguard-rules.pro")

    val proguardOutputDir = buildDir.toPath() / "proguard"
    printmapping(proguardOutputDir / "mapping.txt")
    printseeds(proguardOutputDir / "seeds.txt")
    printusage(proguardOutputDir / "usage.txt")

    val fileName = buildString {
        // [baseName]-[appendix]-[version]-[classifier].[extension]

        append(packageTask.archiveBaseName.get())

        val appendix = packageTask.archiveAppendix.orNull
        if (!appendix.isNullOrBlank()) {
            append('-')
            append(appendix)
        }

        val classifier = packageTask.archiveClassifier.orNull
        if (!classifier.isNullOrBlank()) {
            append('-')
            append(classifier)
        }
        append("-proguard")
        val version = packageTask.archiveVersion.orNull
        if (!version.isNullOrBlank()) {
            append('-')
            append(version)
        }

        val extension = packageTask.archiveExtension.getOrElse("jar")
        append('.')
        append(extension)
    }

    outjars(proguardOutputDir / fileName)

    dependsOn(packageTask)
}

afterEvaluate {
    tasks.named("createDistributable") {
        dependsOn(proguard)
    }
}

compose.desktop {
    application {

        mainClass = "MainKt"

        mainJar.set(
            objects.fileProperty().apply {
                set { proguard.get().outJarFileCollection.singleFile }
            }
        )

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Curriculum Vitae"
            packageVersion = "1.0.0"
            vendor = "CMG Mobile Apps"

            macOS {
                bundleID = "com.cmgapps.mac.curriculumvitae"
                // use scripts/create_icns.sh to create a icons file
                @OptIn(ExperimentalPathApi::class)
                iconFile.set((projectDir.toPath() / "icons" / "CV.icns").toFile())
            }

            windows {
                menuGroup = "Curriculum Vitae"
                // see https://wixtoolset.org/documentation/manual/v3/howtos/general/generate_guids.html
                upgradeUuid = "f4090642-a6c1-4b76-8e5a-957dd09ca6ee"
            }
        }
    }
}
