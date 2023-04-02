/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package com.cmgapps.gradle

import com.cmgapps.gradle.curriculumvitae.AnsiColor
import com.cmgapps.gradle.curriculumvitae.RESET
import com.cmgapps.gradle.curriculumvitae.getTestLog
import org.gradle.api.internal.tasks.testing.results.DefaultTestResult
import org.gradle.api.tasks.testing.TestResult
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test

internal class TestLogShould {

    @Test
    fun `should display summary for success`() {
        val result =
            getTestLog(DefaultTestResult(TestResult.ResultType.SUCCESS, 0, 1, 3, 1, 1, emptyList()))
        assertThat(
            result,
            containsString(
                AnsiColor.Green.toString() + "SUCCESS" + RESET + " (3 tests, " +
                    AnsiColor.Green.toString() + "1 passed" + RESET + ", " +
                    AnsiColor.Red.toString() + "1 failed" + RESET + ", " +
                    AnsiColor.Yellow.toString() + "1 skipped" + RESET + ")",
            ),
        )
    }

    @Test
    fun `should display summary for failure`() {
        val result =
            getTestLog(DefaultTestResult(TestResult.ResultType.FAILURE, 0, 1, 3, 1, 1, emptyList()))
        assertThat(
            result,
            containsString(
                AnsiColor.Red.toString() + "FAILURE" + RESET + " (3 tests, " +
                    AnsiColor.Green.toString() + "1 passed" + RESET + ", " +
                    AnsiColor.Red.toString() + "1 failed" + RESET + ", " +
                    AnsiColor.Yellow.toString() + "1 skipped" + RESET + ")",
            ),
        )
    }

    @Test
    fun `should display summary for skipped`() {
        val result =
            getTestLog(DefaultTestResult(TestResult.ResultType.SKIPPED, 0, 1, 3, 1, 1, emptyList()))
        assertThat(
            result,
            containsString(
                AnsiColor.Yellow.toString() + "SKIPPED" + RESET + " (3 tests, " +
                    AnsiColor.Green.toString() + "1 passed" + RESET + ", " +
                    AnsiColor.Red.toString() + "1 failed" + RESET + ", " +
                    AnsiColor.Yellow.toString() + "1 skipped" + RESET + ")",
            ),
        )
    }
}
