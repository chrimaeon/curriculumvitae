/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

@CacheableTask
abstract class ManifestTransformerTask : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val gitInfoFile: RegularFileProperty

    @get:Input
    var initialVersionCode: Int = VERSION_CODE_NOT_SET

    @get:InputFile
    @get:PathSensitive(PathSensitivity.ABSOLUTE)
    abstract val androidManifest: RegularFileProperty

    @get:OutputFile
    abstract val updatedManifest: RegularFileProperty

    @TaskAction
    fun taskAction() {
        if (initialVersionCode == -1) {
            error("'initialVersionCode' not set")
        }
        val gitVersion = gitInfoFile.get().asFile.readText().toInt()
        val manifest = androidManifest.get().asFile.readText().replace(
            """android:versionCode="\d+"""".toRegex(),
            """android:versionCode="${gitVersion + initialVersionCode}"""",
        )
        updatedManifest.get().asFile.writeText(manifest)
    }

    companion object {
        const val VERSION_CODE_NOT_SET = -1
    }
}
