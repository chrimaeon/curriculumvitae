/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
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

import androidCompileSdkPreviewVersion
import androidMinSdkVersion
import androidTargetSdkPreviewVersion
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.LibraryExtension
import com.android.build.api.dsl.ProductFlavor
import com.android.build.api.dsl.TestExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import testCompletionLog
import buildToolsVersion as depsBuildToolsVersion

internal fun <BF : BuildFeatures, BT : BuildType, DC : DefaultConfig, PF : ProductFlavor> CommonExtension<BF, BT, DC, PF>.commonConfig(
    project: Project,
    block: CommonExtension<BF, BT, DC, PF>.() -> Unit
) {
    compileSdkPreview = androidCompileSdkPreviewVersion
    buildToolsVersion = depsBuildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    testOptions {
        unitTests.all { test ->
            test.useJUnitPlatform()
            test.testLogging {
                events("passed", "skipped", "failed")
            }

            test.afterSuite(project.testCompletionLog())
        }
    }

    block(this)
}

fun ApplicationExtension.baseConfig(project: Project) {
    commonConfig(project) {
        defaultConfig {
            targetSdkPreview = androidTargetSdkPreviewVersion
        }
    }
}

fun TestExtension.baseConfig(project: Project) {
    commonConfig(project) {
        defaultConfig {
            targetSdkPreview = androidTargetSdkPreviewVersion
        }
    }
}

fun LibraryExtension.baseConfig(project: Project) {
    commonConfig(project) {
        defaultConfig {
            targetSdkPreview = androidTargetSdkPreviewVersion
        }
    }
}
