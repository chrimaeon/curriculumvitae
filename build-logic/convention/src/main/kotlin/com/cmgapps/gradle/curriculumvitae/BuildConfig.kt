/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create

interface BuildConfigExtension {
    val baseUrl: Property<String>
    val debugBaseUrls: Property<String>
    val buildYear: Property<String>
    val githubReposUrls: Property<String>
}

fun Project.configureBuildConfig() {
    val extension = extensions.create<BuildConfigExtension>("buildConfig")
    val buildConfigFilesDir: Provider<Directory> =
        project.layout.buildDirectory.dir("generated/buildConfig")

    tasks.register("generateBuildConfig", GenerateBuildConfigTask::class.java) {
        outputDir.set(buildConfigFilesDir)

        this.baseUrl.set(extension.baseUrl)
        this.debugBaseUrls.set(extension.debugBaseUrls)
        this.buildYear.set(extension.buildYear)
        this.githubReposUrl.set(extension.githubReposUrls)
    }
}
