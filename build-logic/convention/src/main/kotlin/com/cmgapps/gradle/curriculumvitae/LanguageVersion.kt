/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import gradle.kotlin.dsl.accessors._245178ab2107cafd8125acd1dd232d15.java
import gradle.kotlin.dsl.accessors._245178ab2107cafd8125acd1dd232d15.kotlin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion

fun Project.configureLanguageVersion() {
    val java11LanguageVersion = JavaLanguageVersion.of(11)

    java {
        toolchain {
            languageVersion.set(java11LanguageVersion)
        }
    }

    kotlin {
        jvmToolchain {
            languageVersion.set(java11LanguageVersion)
        }
    }
}
