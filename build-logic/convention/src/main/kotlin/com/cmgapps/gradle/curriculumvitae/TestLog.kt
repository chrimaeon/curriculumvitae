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

private const val ESC = "\u001B"

/** Control Sequence Introducer */
internal const val CSI = "$ESC["
internal const val RESET = "$ESC[0m"

internal enum class AnsiColor(private val value: Int) {
    Red(31),
    Green(32),
    Yellow(33),
    ;

    override fun toString() = "${CSI}0;${this.value}m"
}

fun Project.testCompletionLog() =
    KotlinClosure2<TestDescriptor, TestResult, Unit>(
        { descriptor, result ->
            if (descriptor.parent == null) {
                logger.lifecycle(getTestLog(result))
            }
        },
        this,
        this,
    )

internal fun getTestLog(result: TestResult): String {
    var specialChars = 0
    val results = buildString {
        append("\u2502")
        append(' ')
        val color = when (result.resultType) {
            TestResult.ResultType.SUCCESS -> AnsiColor.Green

            TestResult.ResultType.FAILURE -> AnsiColor.Red
            TestResult.ResultType.SKIPPED -> AnsiColor.Yellow
            null -> null
        }
        if (color != null) {
            specialChars += color.toString().length
            append(color)
        }
        append(result.resultType)
        if (color != null) {
            specialChars += RESET.length
            append(RESET)
        }
        append(" (")
        append(result.testCount)
        append(" tests, ")

        val hasSuccess = result.successfulTestCount > 0
        if (hasSuccess) {
            val colorSequence = AnsiColor.Green.toString()
            specialChars += colorSequence.length
            append(colorSequence)
        }
        append(result.successfulTestCount)
        append(" passed")
        if (hasSuccess) {
            specialChars += RESET.length
            append(RESET)
        }
        append(", ")

        val hasFailed = result.failedTestCount > 0
        if (hasFailed) {
            val colorSequence = AnsiColor.Red.toString()
            specialChars += colorSequence.length
            append(colorSequence)
        }
        append(result.failedTestCount)
        append(" failed")
        if (hasFailed) {
            specialChars += RESET.length
            append(RESET)
        }
        append(", ")

        val hasSkipped = result.skippedTestCount > 0
        if (hasSkipped) {
            val colorSequence = AnsiColor.Yellow.toString()
            specialChars += colorSequence.length
            append(colorSequence)
        }
        append(result.skippedTestCount)
        append(" skipped")
        if (hasSkipped) {
            specialChars += RESET.length
            append(RESET)
        }

        append(") \u2502")
    }
    return buildString((results.length) * 3) {
        append('\n')
        append('\u250C')
        repeat(results.length - specialChars - 2) {
            append('\u2500')
        }
        append('\u2510')

        append('\n')
        append(results)
        append('\n')

        append('\u2514')
        repeat(results.length - specialChars - 2) {
            append('\u2500')
        }
        append('\u2518')
    }
}
