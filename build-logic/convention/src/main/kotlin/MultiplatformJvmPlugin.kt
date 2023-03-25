/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.javaLanguageVersion
import com.cmgapps.gradle.curriculumvitae.testCompletionLog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.get
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

@Suppress("unused")
class MultiplatformJvmPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.multiplatform")
            }

            extensions.configure<KotlinMultiplatformExtension> {
                jvmToolchain {
                    languageVersion.set(javaLanguageVersion)
                }

                jvm {
                    withJava()

                    testRuns["test"].executionTask.configure {
                        useJUnitPlatform()
                        testLogging {
                            events("passed", "skipped", "failed")
                        }
                        afterSuite(testCompletionLog())
                    }

                    jvmToolchain {
                        languageVersion.set(javaLanguageVersion)
                    }
                }
            }
        }
    }
}
