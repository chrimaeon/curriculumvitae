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

@file:Suppress("SpellCheckingInspection")

import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec
import org.tomlj.Toml
import org.tomlj.TomlParseResult
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.Path
import kotlin.io.path.div

@OptIn(ExperimentalPathApi::class)
private val toml: TomlParseResult =
    with(Path(System.getProperty("user.dir")) / "gradle" / "libs.versions.toml") {
        Toml.parse(toFile().inputStream()).apply {
            if (hasErrors()) {
                errors().forEach { error -> System.err.println(error.toString()) }
                throw RuntimeException()
            }
        }
    }

val protobufPluginVersion = toml.getString("versions.plugin-protobuf")
val kotlinVersion = toml.getString("versions.kotlin")
val ktlintVersion = toml.getString("versions.ktlint")
val licensesVersion = toml.getString("versions.plugin-licenses")

object Plugins {
    val androidGradlePlugin =
        "com.android.tools.build:gradle:" + toml.getString("versions.plugin-androidGradle")
    val kotlinGradlePlugin =
        "org.jetbrains.kotlin:kotlin-gradle-plugin:" + toml.getString("versions.kotlin")
    val hiltGradlePlugin =
        "com.google.dagger:hilt-android-gradle-plugin:" + toml.getString("versions.hilt")
    val appEngine =
        "com.google.cloud.tools:appengine-gradle-plugin:" + toml.getString("versions.plugin-appEngine")
}

object Libs {
    object Misc {
        val ktlint = "com.pinterest:ktlint:$ktlintVersion"
    }
}

val PluginDependenciesSpec.benManesVersions: PluginDependencySpec
    get() = id("com.github.ben-manes.versions") version toml.getString("versions.plugin-benManesVersions")

val PluginDependenciesSpec.ktlint: PluginDependencySpec
    get() = id("com.cmgapps.gradle.ktlint")
