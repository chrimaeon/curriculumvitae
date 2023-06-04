/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.LibraryExtension
import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.configureLanguageVersion
import com.cmgapps.gradle.curriculumvitae.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidLibraryConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this, libs)
            }

            configureLanguageVersion()
        }
    }
}
