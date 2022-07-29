/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

fun isCi(): Boolean = System.getenv("CI") != null

fun String.runCommand(
    workingDir: File = File("."),
    timeoutAmount: Long = 60,
    timeoutUnit: TimeUnit = TimeUnit.SECONDS,
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
