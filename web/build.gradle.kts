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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.0.0-alpha3"
    ktlint
}

val generatedFilesDir: Provider<Directory> = project.layout.buildDirectory.dir("generated")

kotlin {
    js(IR) {
        browser {
            commonWebpackConfig {
                outputFileName = "app.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        named("jsMain") {
            dependencies {
                implementation(compose.web.widgets)
                implementation(compose.web.core)
                implementation(compose.runtime)

                implementation(projects.common)

                implementation(libs.bundles.ktor.client)
                implementation(libs.koin)
            }
            this.kotlin.srcDir(generatedFilesDir)
        }
    }

    targets.all {
        compilations.all {

            compileKotlinTaskProvider {
                dependsOn(generateBuildConfig)
            }
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
            }
        }
    }
}

val generateBuildConfig by tasks.registering {
    val outputDir = generatedFilesDir

    val baseUrl by configProperties()
    val debugBaseUrls by configProperties()
    inputs.property("baseUrl", baseUrl)
    outputs.dir(outputDir)

    doLast {
        FileSpec.builder("com.cmgapps.web.curriculumvitae", "BuildConfig")
            .addProperty(
                PropertySpec.builder(
                    "baseUrl",
                    String::class,
                    KModifier.INTERNAL,
                    KModifier.CONST
                ).initializer("%S", baseUrl).build()
            ).addProperty(
                PropertySpec.builder(
                    "debugBaseUrls",
                    List::class.asClassName().parameterizedBy(String::class.asClassName()),
                    KModifier.INTERNAL,
                ).initializer(
                    "%N(%L)",
                    MemberName("kotlin.collections", "listOf"),
                    debugBaseUrls.split(",").joinToString { """"$it"""" }
                ).build()
            )
            .build()
            .writeTo(outputDir.get().asFile)
    }
}
