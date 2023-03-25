/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.LibraryExtension
import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.getVersion
import com.cmgapps.gradle.curriculumvitae.libs
import com.cmgapps.gradle.curriculumvitae.testCompletionLog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class MultiplatformAndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("com.android.library")
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                jvm {
                    testRuns["test"].executionTask.configure {
                        useJUnitPlatform()
                        testLogging {
                            events("passed", "skipped", "failed")
                        }
                        afterSuite(testCompletionLog())
                    }
                }

                android()
            }

            extensions.configure<LibraryExtension> {
                configureKotlinAndroid(this, libs)

                defaultConfig.targetSdk = libs.getVersion("androidTargetSdk").toInt()
            }
        }
    }
}
