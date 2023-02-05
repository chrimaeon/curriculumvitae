/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.configureKotlinAndroid
import com.cmgapps.gradle.curriculumvitae.configureLanguageVersion
import com.cmgapps.gradle.curriculumvitae.getVersion
import com.cmgapps.gradle.curriculumvitae.libs

plugins {
    id("com.android.test")
    kotlin("android")
}

android {
    configureKotlinAndroid(this, libs)

    defaultConfig {
        targetSdk = libs.getVersion("androidTargetSdk").toInt()
    }
}

configureLanguageVersion()
