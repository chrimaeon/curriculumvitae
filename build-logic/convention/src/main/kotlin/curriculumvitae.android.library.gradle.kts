/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.androidTargetSdkVersion
import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    configureKotlinAndroid(this, useJUnitPlatform = false)

    defaultConfig {
        targetSdk = androidTargetSdkVersion
    }
}
