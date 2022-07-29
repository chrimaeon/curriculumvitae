/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class GitVersionTask : DefaultTask() {

    @get:OutputFile
    abstract val gitVersionOutputFile: RegularFileProperty

    @TaskAction
    fun taskAction() {
        val gitVersion = "git rev-list --count HEAD".runCommand(workingDir = project.rootDir)
        gitVersionOutputFile.asFile.get().writeText(gitVersion)
    }
}
