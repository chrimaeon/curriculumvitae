/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.getVersion
import com.cmgapps.gradle.curriculumvitae.libs
import com.cmgapps.gradle.curriculumvitae.testCompletionLog
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

configure<KotlinMultiplatformExtension> {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
            afterSuite(testCompletionLog())
        }
    }

    android()
}

android {
    configureKotlinAndroid(this, libs)

    defaultConfig {
        targetSdk = libs.getVersion("androidTargetSdk").toInt()
    }
}
