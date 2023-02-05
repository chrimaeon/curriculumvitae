/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.Project
import org.gradle.api.attributes.Bundling
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.named

@Suppress("UnstableApiUsage")
fun Project.configureKtlint() {
    val ktlintConfiguration = configurations.create("ktlint")

    tasks {
        val inputFiles = fileTree("src") {
            include("**/*.kt")
        }
        val outputDir = "$buildDir/reports"

        register("ktlintFormat", JavaExec::class.java) {
            inputs.files(inputFiles)
            outputs.dir(outputDir)

            group = "Formatting"
            description = "Fix Kotlin code style deviations."
            mainClass.set("com.pinterest.ktlint.Main")
            classpath = ktlintConfiguration
            args = listOf("-F", "src/**/*.kt")
        }

        val ktlintTask = register("ktlint", JavaExec::class.java) {
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

        named("check") {
            dependsOn(ktlintTask)
        }
    }

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
