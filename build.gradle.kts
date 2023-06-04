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

@file:Suppress("UnstableApiUsage", "SpellCheckingInspection")

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnLockMismatchReport
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnPlugin
import org.jetbrains.kotlin.gradle.targets.js.yarn.YarnRootExtension

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath(libs.appEnginePlugin)
        classpath(libs.sqldelightPlugin)
        // needed because moshi uses okio:2.x and wire plugin requires okio:3.x
        classpath(libs.squareup.okio)
    }
}

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.benManesVersionsGradle)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.android.application) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.android.library) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.android.test) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlin.jvm) apply false
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.hilt) apply false
}

allprojects {
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xmaxerrs", "500"))
        }
    }

    val localProject: Project = this

    configurations.all {
        resolutionStrategy {
            val composeVersion = libs.versions.composeMultiplatform

            with(libs.hamcrest.get()) {
                "${this.module.group}:${this.module.name}:${this.versionConstraint.displayName}"
            }.let {
                force(it)
                dependencySubstitution {
                    substitute(module("org.hamcrest:hamcrest-core")).using(module(it))
                    substitute(module("org.hamcrest:hamcrest-integration")).using(module(it))
                    substitute(module("org.hamcrest:hamcrest-library")).using(module(it))
                }
            }
            eachDependency {
                // Use released version of Compose for Android
                // remove once compose web is in "main" repository
                val useAndroidComposeVersion =
                    localProject.name.contains("app") || localProject.name == "wearable"
                val isComposeGroup = requested.module.group.startsWith("org.jetbrains.compose")
                val isComposeCompiler =
                    requested.module.group.startsWith("org.jetbrains.compose.compiler")
                if (useAndroidComposeVersion && isComposeGroup && !isComposeCompiler) {
                    logger.info(":${localProject.name} is using version ${composeVersion.get()} for ${requested.module}")
                    useVersion(composeVersion.get())
                }
            }
        }
    }
}

tasks {
    named<Wrapper>("wrapper") {
        gradleVersion = libs.versions.gradle.get()
        distributionType = Wrapper.DistributionType.ALL
    }

    named<DependencyUpdatesTask>("dependencyUpdates") {

        revision = "release"
        rejectVersionIf {
            listOf("alpha", "beta", "rc", "cr", "m", "eap", "dev").any { qualifier ->
                """(?i).*[.-]?$qualifier[.\d-]*""".toRegex()
                    .containsMatchIn(candidate.version)
            }
        }
        gradleReleaseChannel = CURRENT.id
    }
}

rootProject.plugins.withType(YarnPlugin::class.java) {
    rootProject.configure<YarnRootExtension> {
        yarnLockMismatchReport = YarnLockMismatchReport.WARNING
        // TODO WASM not handling it correctly
        reportNewYarnLock = false
    }
}
