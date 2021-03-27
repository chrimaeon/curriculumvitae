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

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel.CURRENT
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(Plugins.androidGradlePlugin)
        classpath(Plugins.kotlinGradlePlugin)
        classpath(Plugins.hiltGradlePlugin)
    }
}

plugins {
    benManesVersions
}

allprojects {
    tasks {
        withType(KotlinCompile::class) {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }
}

tasks {
    register<Delete>("clean") {
        delete(rootProject.buildDir)
    }

    named<Wrapper>("wrapper") {
        gradleVersion = "6.8.3"
        distributionType = Wrapper.DistributionType.ALL
    }

    named<DependencyUpdatesTask>("dependencyUpdates") {
        revision = "release"
        rejectVersionIf {
            !candidate.group.contains("compose|com\\.google\\.dagger|com\\.android\\.tools\\.build".toRegex()) &&
                !candidate.module.contains("compose") &&
                listOf("alpha", "beta", "rc", "cr", "m", "eap").any { qualifier ->
                    """(?i).*[.-]?$qualifier[.\d-]*""".toRegex()
                        .containsMatchIn(candidate.version)
                }
        }
        gradleReleaseChannel = CURRENT.id
    }
}
