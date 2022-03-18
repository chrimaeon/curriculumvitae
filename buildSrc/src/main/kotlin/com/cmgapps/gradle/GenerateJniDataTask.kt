/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject

@Suppress("UnstableApiUsage")
open class GenerateJniDataTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @InputFile
    val source: RegularFileProperty = objects.fileProperty()

    @OutputFile
    val outputFile: RegularFileProperty = objects.fileProperty()

    @TaskAction
    fun generateJniData() {
        val sourceFile = source.get().asFile
        val outputFile = outputFile.get().asFile
        logger.info(
            "Writing JNI data from {} to {}",
            sourceFile.absolutePath,
            outputFile.absolutePath
        )
        sourceFile writeXorTo outputFile
    }
}
