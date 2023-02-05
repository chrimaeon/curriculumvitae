/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

package com.cmgapps.gradle.curriculumvitae

import com.android.build.api.dsl.BuildFeatures
import com.android.build.api.dsl.BuildType
import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DefaultConfig
import com.android.build.api.dsl.ProductFlavor
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog

internal fun <BF : BuildFeatures, BT : BuildType, DC : DefaultConfig, PF : ProductFlavor> Project.configureKotlinAndroid(
    commonExtension: CommonExtension<BF, BT, DC, PF>,
    libs: VersionCatalog,
) {
    commonExtension.apply {
        compileSdk = libs.getVersion("androidCompileSdk").toInt()
        buildToolsVersion = libs.getVersion("androidBuildTools")

        defaultConfig {
            minSdk = libs.getVersion("androidMinSdk").toInt()
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
    }
}
