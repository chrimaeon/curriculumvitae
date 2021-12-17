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

import buildToolsVersion as depsBuildToolsVersion

plugins {
    id("com.android.application")
    kotlin("android")
    ktlint
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = depsBuildToolsVersion

    defaultConfig {
        applicationId = "com.cmgapps.wear.curriculumvitae"
        minSdk = androidWearMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = 1
        versionName = "1.0.0"

        resourceConfigurations.addAll(listOf("en", "de"))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    packagingOptions {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

dependencies {
    implementation(projects.common)
    implementation(projects.commonCompose)
    implementation(libs.playservices.wearable)
    implementation(libs.bundles.compose.wear)
    implementation(libs.koin.compose)
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.placeholder)
    implementation(libs.accompanist.pager)
    implementation(libs.accompanist.pagerIndicators)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.extJunit)
    androidTestImplementation(libs.androidx.coreTesting)
    androidTestImplementation(libs.compose.uiTest)
    androidTestImplementation(libs.koin.test)
    androidTestImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.ktor.client.serialization)
    androidTestImplementation(libs.sqldelight.driver.android)
}
