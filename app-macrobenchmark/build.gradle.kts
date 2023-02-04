/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.androidTargetSdkVersion

plugins {
    id("curriculumvitae.android.test")
    id("ktlint")
}

android {
    namespace = "com.cmgapps.android.curriculumvitae.macrobenchmark"
    defaultConfig {
        targetSdk = androidTargetSdkVersion

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val debugSigningConfig = signingConfigs.named("debug") {
        storeFile = rootDir.resolve("keystore").resolve("debug.keystore")
    }

    buildTypes {
        register("benchmark") {
            isDebuggable = true
            signingConfig = debugSigningConfig.get()
        }
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true
}

androidComponents {
    beforeVariants {
        // enable only the benchmark buildType, since we only want to measure close to release performance
        it.enable = it.buildType == "benchmark"
    }
}

dependencies {
    implementation(libs.androidx.testRunner)
    implementation(libs.androidx.extJunit)
    implementation(libs.androidx.benchmarkMacroJunit4)
    implementation(libs.androidx.uiAutomator)
}
