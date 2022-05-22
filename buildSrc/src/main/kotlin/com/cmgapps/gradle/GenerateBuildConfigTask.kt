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

package com.cmgapps.gradle

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
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

    @TaskAction
    fun generateFile() {
        FileSpec.builder("com.cmgapps.common.curriculumvitae", "BuildConfig")
            .addProperty(
                PropertySpec.builder(
                    "BaseUrl",
                    String::class,
                    KModifier.CONST
                ).initializer("%S", baseUrl.get()).build()
            ).addProperty(
                PropertySpec.builder(
                    "DebugBaseUrls",
                    List::class.asClassName().parameterizedBy(String::class.asClassName()),
                ).initializer(
                    "%N(%L)",
                    MemberName("kotlin.collections", "listOf"),
                    debugBaseUrls.get().split(",").joinToString { """"$it"""" }
                ).build()
            ).addProperty(
                PropertySpec.builder(
                    "BuildYear",
                    String::class,
                    KModifier.CONST
                ).initializer("%S", buildYear.get()).build()
            )
            .build()
            .writeTo(outputDir.get().asFile)
    }
}
