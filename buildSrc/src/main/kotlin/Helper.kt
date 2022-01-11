/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.gradle.api.Project
import org.gradle.api.tasks.testing.TestDescriptor
import org.gradle.api.tasks.testing.TestResult
import org.gradle.kotlin.dsl.KotlinClosure2
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun isCi(): Boolean = System.getenv("CI") != null

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS
): String =
    ProcessBuilder(split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)".toRegex()))
        .directory(workingDir)
        .redirectOutput(ProcessBuilder.Redirect.PIPE)
        .redirectError(ProcessBuilder.Redirect.PIPE)
        .start()
        .apply { waitFor(timeoutAmount, timeoutUnit) }
        .run {
            val error = errorStream.bufferedReader().readText().trim()
            if (error.isNotEmpty()) {
                throw IOException(error)
            }
            inputStream.bufferedReader().readText().trim()
        }

fun Project.testCompletionLog() =
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
