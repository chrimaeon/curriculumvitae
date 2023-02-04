/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.configureLanguageVersion() {
    val java11LanguageVersion = JavaLanguageVersion.of(11)

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(java11LanguageVersion)
        }
    }

    configure<KotlinAndroidProjectExtension> {
        jvmToolchain {
            languageVersion.set(java11LanguageVersion)
        }
    }
}
