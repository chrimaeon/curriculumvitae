/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.cmgapps.gradle.curriculumvitae.BuildConfigExtension
import com.cmgapps.gradle.curriculumvitae.GenerateBuildConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.create

class BuildConfigPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create<BuildConfigExtension>("buildConfig")
            val buildConfigFilesDir: Provider<Directory> =
                project.layout.buildDirectory.dir("generated/buildConfig")

            tasks.register("generateBuildConfig", GenerateBuildConfigTask::class.java) {
                outputDir.set(buildConfigFilesDir)

                this.baseUrl.set(extension.baseUrl)
                this.debugBaseUrls.set(extension.debugBaseUrls)
                this.buildYear.set(extension.buildYear)
                this.githubReposUrl.set(extension.githubReposUrl)
            }
        }
    }
}
