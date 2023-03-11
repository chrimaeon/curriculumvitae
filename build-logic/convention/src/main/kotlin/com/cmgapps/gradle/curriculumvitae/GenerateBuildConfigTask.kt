/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@CacheableTask
abstract class GenerateBuildConfigTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {
    @get:OutputDirectory
    val outputDir: DirectoryProperty = objects.directoryProperty()

    @get:Input
    val baseUrl: Property<String> = objects.property()

    @get:Input
    val debugBaseUrls: Property<String> = objects.property()

    @get:Input
    val buildYear: Property<String> = objects.property()

    @get:Input
    val githubReposUrl: Property<String> = objects.property()

    @TaskAction
    fun generateFile() {
        FileSpec.builder("com.cmgapps.common.curriculumvitae", "BuildConfig")
            .addProperty(
                PropertySpec.builder(
                    "BaseUrl",
                    String::class,
                    KModifier.CONST,
                ).initializer("%S", baseUrl.get()).build(),
            ).addProperty(
                PropertySpec.builder(
                    "DebugBaseUrls",
                    List::class.parameterizedBy(String::class),
                ).initializer(
                    "%N(%L)",
                    MemberName("kotlin.collections", "listOf"),
                    debugBaseUrls.get().split(",").joinToString { """"$it"""" },
                ).build(),
            ).addProperty(
                PropertySpec.builder(
                    "BuildYear",
                    String::class,
                    KModifier.CONST,
                ).initializer("%S", buildYear.get()).build(),
            ).addProperty(
                PropertySpec.builder(
                    "GithubReposUrl",
                    String::class,
                    KModifier.CONST,
                ).initializer("%S", githubReposUrl.get()).build(),
            )
            .build()
            .writeTo(outputDir.get().asFile)
    }
}
