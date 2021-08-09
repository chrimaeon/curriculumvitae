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
    }
    dependencies {
        val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

        classpath(libs.findDependency("plugin-android").orElseThrow())
        classpath(libs.findDependency("plugin-kotlin").orElseThrow())
        classpath(libs.findDependency("plugin-hiltAndroid").orElseThrow())
        classpath(libs.findDependency("plugin-appEngine").orElseThrow())
    }
}

plugins {
    benManesVersions
}

allprojects {
    tasks {
        withType<JavaCompile> {
            options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xmaxerrs", "500"))
        }

        withType<KotlinCompile> {
            kotlinOptions {
                jvmTarget = "1.8"
                freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
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
    // register<Delete>("clean") {
    //     delete(rootProject.buildDir)
    // }

    named<Wrapper>("wrapper") {
        gradleVersion = libs.versions.gradle.get()
        distributionType = Wrapper.DistributionType.ALL
    }

    named<DependencyUpdatesTask>("dependencyUpdates") {

        revision = "release"
        rejectVersionIf {

            fun String.filterGroup(): Boolean = listOf(
                "androidx.datastore",
                "com.google.cloud",
                "com.google.devtools.ksp",
                "androidx.lifecycle",
            ).any { this.contains(it) }

            fun String.filterModule(): Boolean = listOf("compose").any { this.contains(it) }

            fun ModuleComponentIdentifier.rejectedVersion(): Boolean =
                listOf("alpha", "beta", "rc", "cr", "m", "eap").any { qualifier ->
                    """(?i).*[.-]?$qualifier[.\d-]*""".toRegex()
                        .containsMatchIn(version)
                }

            fun ModuleComponentIdentifier.filter(): ModuleComponentIdentifier? =
                if (group.filterGroup() || module.filterModule()) null else this

            candidate.filter()?.rejectedVersion() ?: false
        }
        gradleReleaseChannel = CURRENT.id
    }
}
