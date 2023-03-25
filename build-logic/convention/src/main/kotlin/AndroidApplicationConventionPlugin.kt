/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.ApplicationExtension
import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.configureLanguageVersion
import com.cmgapps.gradle.curriculumvitae.getVersion
import com.cmgapps.gradle.curriculumvitae.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.application")
                apply("org.jetbrains.kotlin.android")
            }

            extensions.configure<ApplicationExtension> {
                configureKotlinAndroid(this, libs)

                defaultConfig.targetSdk = libs.getVersion("androidTargetSdk").toInt()
            }

            configureLanguageVersion()
        }
    }
}
