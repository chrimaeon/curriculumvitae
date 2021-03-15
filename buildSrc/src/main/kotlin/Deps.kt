import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

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

object Gradle {
    const val gradleVersion = "6.8.3"
}

const val accompanistVersion = "0.6.2"
const val benManesVersionsVersion = "0.38.0"
const val composeVersion = "1.0.0-beta02"
const val hiltVersion = "2.33-beta"
const val kotlinVersion = "1.4.31"
const val roomVersion = "2.2.6"

object Plugins {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha10"
    const val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
    const val hiltGradlePlugin = "com.google.dagger:hilt-android-gradle-plugin:$hiltVersion"
}

object Libs {
    object AndroidX {
        const val coreKtx = "androidx.core:core-ktx:1.3.2"
        const val composeUi = "androidx.compose.ui:ui:$composeVersion"
        const val composeUiTooling = "androidx.compose.ui:ui-tooling:$composeVersion"
        const val composeFoundation = "androidx.compose.foundation:foundation:$composeVersion"
        const val composeMaterial = "androidx.compose.material:material:$composeVersion"
        const val composeMaterialIconsExtended =
            "androidx.compose.material:material-icons-extended:$composeVersion"
        const val composeActivity = "androidx.activity:activity-compose:1.3.0-alpha04"
        const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha03"
        const val composeLiveData = "androidx.compose.runtime:runtime-livedata:$composeVersion"
        const val composeNavigation = "androidx.navigation:navigation-compose:1.0.0-alpha09"

        const val room = "androidx.room:room-ktx:$roomVersion"
        const val roomCompiler = "androidx.room:room-compiler:$roomVersion"

        const val lifecycleLivedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.0"
    }

    object Misc {
        const val ktlint = "com.pinterest:ktlint:0.40.0"
        const val accompanistInsets =
            "dev.chrisbanes.accompanist:accompanist-insets:$accompanistVersion"
        const val accompanistCoil =
            "dev.chrisbanes.accompanist:accompanist-coil:$accompanistVersion"
    }
}

val PluginDependenciesSpec.benManesVersions: PluginDependencySpec
    get() = id("com.github.ben-manes.versions") version benManesVersionsVersion
