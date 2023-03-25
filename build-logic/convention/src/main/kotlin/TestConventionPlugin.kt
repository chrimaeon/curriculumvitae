/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.cmgapps.gradle.curriculumvitae.testCompletionLog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test

class TestConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("org.jetbrains.kotlin.jvm")
            }
            tasks.named("test", Test::class.java) {
                useJUnitPlatform()
                testLogging {
                    events("passed", "skipped", "failed")
                }
                afterSuite(testCompletionLog())
            }
        }
    }
}
