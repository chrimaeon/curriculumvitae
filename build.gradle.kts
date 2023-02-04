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
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

    dependencies {
        classpath(libs.android.gradlePlugin)
        classpath(libs.kotlin.gradlePlugin)
        classpath(libs.android.hiltPlugin)
        classpath(libs.appEnginePlugin)
        classpath(libs.sqldelightPlugin)
    }
}

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.benManesVersionsGradle)
}

allprojects {
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xmaxerrs", "500"))
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_11.toString()
                freeCompilerArgs = freeCompilerArgs + listOf("-opt-in=kotlin.RequiresOptIn")
            }
        }
    }

    configurations.all {
        resolutionStrategy {
            @Suppress("UnstableApiUsage")
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
            listOf("alpha", "beta", "rc", "cr", "m", "eap").any { qualifier ->
                """(?i).*[.-]?$qualifier[.\d-]*""".toRegex()
                    .containsMatchIn(candidate.version)
            }
        }
        gradleReleaseChannel = CURRENT.id
    }

    // Workaround to run "check" task on all modules
    // see https://youtrack.jetbrains.com/issue/KT-49202
    register("checkWorkaround") {
        dependsOn(
            ":common:ktlint",
            ":common:jvmTest",
            ":common:jsTest",
            ":common:iosSimulatorArm64Test",
            ":common:verifySqlDelightMigration",
        )
        dependsOn(":app:check")
        dependsOn(":wearable:check")
        dependsOn(":backend:check")
        dependsOn(":desktop:check")
        dependsOn(":web:check")
    }
}
