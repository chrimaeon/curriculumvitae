/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.named

@Suppress("UnstableApiUsage", "unused")
class KtlintPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val ktlintConfiguration = configurations.create("ktlint")

            val inputFiles = fileTree("src") {
                include("**/*.kt")
            }
            val outputDir = "$buildDir/reports"

            tasks.register("ktlintFormat", JavaExec::class.java) {
                inputs.files(inputFiles)
                outputs.dir(outputDir)

                group = "Formatting"
                description = "Fix Kotlin code style deviations."
                mainClass.set("com.pinterest.ktlint.Main")
                classpath = ktlintConfiguration
                args = listOf("-F", "src/**/*.kt")
            }

            val ktlintTask = tasks.register("ktlint", JavaExec::class.java) {
                inputs.files(inputFiles)
                outputs.dir(outputDir)

                group = "Verification"
                description = "Check Kotlin code style."
                mainClass.set("com.pinterest.ktlint.Main")
                classpath = ktlintConfiguration
                args = listOf(
                    "src/**/*.kt",
                    "--reporter=plain",
                    "--reporter=html,output=$outputDir/ktlint.html",
                )
            }

            tasks.named("check") {
                dependsOn(ktlintTask)
            }

            val libs = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

            dependencies {
                ktlintConfiguration(
                    libs.findLibrary("ktlint")
                        .orElseThrow { NoSuchElementException("ktlint not found in version catalog") },
                ) {
                    attributes {
                        attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
                    }
                }
            }
        }
    }
}
