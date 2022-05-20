/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.Project
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.KotlinClosure2

internal fun Project.testCompletionLog() =
    KotlinClosure2<TestDescriptor, TestResult, Unit>(
        { descriptor, result ->
            if (descriptor.parent == null) {
                val results =
                    "\u2502 ${result.resultType} (${result.testCount} tests, ${result.successfulTestCount} passed, ${result.failedTestCount} failed, ${result.skippedTestCount} skipped) \u2502"
                val log = buildString(results.length * 3) {
                    append('\n')
                    append('\u250C')
                    repeat(results.length - 2) {
                        append('\u2500')
                    }
                    append('\u2510')

                    append('\n')
                    append(results)
                    append('\n')

                    append('\u2514')
                    repeat(results.length - 2) {
                        append('\u2500')
                    }
                    append('\u2518')
                }
                logger.lifecycle(log)
            }
        },
        this,
        this
    )
