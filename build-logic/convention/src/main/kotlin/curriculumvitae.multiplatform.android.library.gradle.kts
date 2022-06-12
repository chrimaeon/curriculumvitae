/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.androidTargetSdkVersion
import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.testCompletionLog

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
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
    configureKotlinAndroid(this)

    defaultConfig {
        targetSdk = androidTargetSdkVersion
    }
}
