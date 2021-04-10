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

import org.gradle.kotlin.dsl.version
import org.gradle.plugin.use.PluginDependenciesSpec
import org.gradle.plugin.use.PluginDependencySpec

object Gradle {
    const val gradleVersion = "6.8.3"
}

const val accompanistVersion = "0.6.2"
const val benManesVersionsVersion = "0.38.0"
const val composeVersion = "1.0.0-beta04"
const val hiltVersion = "2.34-beta"
const val kotlinVersion = "1.4.32"
const val roomVersion = "2.2.6"
const val retrofitVersion = "2.9.0"
const val ktorVersion = "1.5.3"
const val nettyTcnVersion = "2.0.38.Final"

object Plugins {
    const val androidGradlePlugin = "com.android.tools.build:gradle:7.0.0-alpha14"
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
        const val composeActivity = "androidx.activity:activity-compose:1.3.0-alpha06"
        const val composeViewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:1.0.0-alpha04"
        const val composeLiveData = "androidx.compose.runtime:runtime-livedata:$composeVersion"
        const val composeNavigation = "androidx.navigation:navigation-compose:1.0.0-alpha10"
        const val composeConstraintLayout =
            "androidx.constraintlayout:constraintlayout-compose:1.0.0-alpha05"

        // const val room = "androidx.room:room-ktx:$roomVersion"
        // const val roomCompiler = "androidx.room:room-compiler:$roomVersion"

        const val lifecycleLivedataKtx = "androidx.lifecycle:lifecycle-livedata-ktx:2.3.1"
    }

    object Misc {
        const val ktlint = "com.pinterest:ktlint:0.41.0"

        const val accompanistInsets =
            "dev.chrisbanes.accompanist:accompanist-insets:$accompanistVersion"
        const val accompanistCoil =
            "dev.chrisbanes.accompanist:accompanist-coil:$accompanistVersion"

        const val timber = "com.jakewharton.timber:timber:4.7.1"

        const val logtag = "com.cmgapps.logtag:log-tag:0.1.0"
        const val logtagProcessor = "com.cmgapps.logtag:processor:0.1.0"

        const val hiltAndroid = "com.google.dagger:hilt-android:$hiltVersion"
        const val hiltCompiler = "com.google.dagger:hilt-compiler:$hiltVersion"

        const val kotlinxJsonSerialization =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:1.1.0"

        const val retrofit = "com.squareup.retrofit2:retrofit:$retrofitVersion"
        const val retrofitKotlinSerialization =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0"
        const val okHttpBom = "com.squareup.okhttp3:okhttp-bom:4.9.1"
        const val okHttp = "com.squareup.okhttp3:okhttp"
        const val okHttpLoggingInterceptor = "com.squareup.okhttp3:logging-interceptor"
    }

    object Testing {
        const val androidCoreTesting = "androidx.arch.core:core-testing:2.1.0"
        const val junitBom = "org.junit:junit-bom:5.7.1"
        const val junitJupiter = "org.junit.jupiter:junit-jupiter"
        const val mockito = "org.mockito:mockito-junit-jupiter:3.9.0"
        const val coroutineTest = "org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3"
        const val hamcrest = "org.hamcrest:hamcrest:2.2"
        const val extJunit = "androidx.test.ext:junit:1.1.2"
        const val espresso = "androidx.test.espresso:espresso-core:3.3.0"
        const val composeUiTest = "androidx.compose.ui:ui-test-junit4:$composeVersion"
        const val hiltTesting = "com.google.dagger:hilt-android-testing:$hiltVersion"
        const val archCoreTesting = "androidx.arch.core:core-testing:2.1.0"
        const val retrofitMockServer = "com.squareup.retrofit2:retrofit-mock:$retrofitVersion"
        const val ktorTesting = "io.ktor:ktor-server-test-host:$ktorVersion"
    }

    object Backend {
        const val ktorNettyServer = "io.ktor:ktor-server-netty:$ktorVersion"
        const val logback = "ch.qos.logback:logback-classic:1.2.3"
        const val ktorSerialization = "io.ktor:ktor-serialization:$ktorVersion"
        const val ktorHtml = "io.ktor:ktor-html-builder:$ktorVersion"
        const val kotlinCss = "org.jetbrains:kotlin-css:1.0.0-pre.153-kotlin-1.4.32"
        const val nettyTcn = "io.netty:netty-tcnative:$nettyTcnVersion"
        const val nettyTcnBoringSll = "io.netty:netty-tcnative-boringssl-static:$nettyTcnVersion"
    }
}

val PluginDependenciesSpec.benManesVersions: PluginDependencySpec
    get() = id("com.github.ben-manes.versions") version benManesVersionsVersion

val PluginDependenciesSpec.ktlint: PluginDependencySpec
    get() = id("com.cmgapps.gradle.ktlint")
