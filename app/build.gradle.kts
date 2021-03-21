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

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version kotlinVersion
    ktlint
    id("dagger.hilt.android.plugin")
}

val xorDirPath = "generated/source/xor"

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        minSdkVersion(24)
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl = if (System.getenv("CI") != null) {
            System.getenv("CV_BASE_URL")
        } else {
            Properties().apply {
                rootDir.resolve("api.properties").inputStream().use {
                    load(it)
                }
            }["baseUrl"]
        }

        resConfigs("en", "de")

        buildConfigField("String", "BASE_URL", """"$baseUrl"""")
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = composeVersion
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    sourceSets {
        named("main") {
            java.srcDir(buildDir.resolve(xorDirPath))
        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}

tasks {
    val generateEmailAddress by registering {
        val outputDir = project.buildDir.resolve(xorDirPath)

        val emailAddress = "christian.grach@gmx.at"
        inputs.property("email", emailAddress)
        val packageName = android.defaultConfig.applicationId ?: error("app id not set")
        inputs.property("packageName", packageName)

        outputs.dir(outputDir)

        doLast {
            generateEmailAddress(emailAddress, packageName, outputDir)
        }
    }

    withType<KotlinCompile> {
        dependsOn(generateEmailAddress)
    }
}

dependencies {
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.lifecycleLivedataKtx)

    implementation(Libs.AndroidX.composeUi)
    implementation(Libs.AndroidX.composeUiTooling)
    implementation(Libs.AndroidX.composeFoundation)
    implementation(Libs.AndroidX.composeMaterial)
    implementation(Libs.AndroidX.composeMaterialIconsExtended)
    implementation(Libs.AndroidX.composeActivity)
    implementation(Libs.AndroidX.composeViewModel)
    implementation(Libs.AndroidX.composeLiveData)

    implementation(Libs.AndroidX.composeNavigation)

    implementation(Libs.Misc.accompanistInsets)
    implementation(Libs.Misc.accompanistCoil)
    implementation(Libs.Misc.timber)

    implementation(Libs.Misc.logtag)
    kapt(Libs.Misc.logtagProcessor)

    implementation(Libs.Misc.hiltAndroid)
    kapt(Libs.Misc.hiltCompiler)

    implementation(platform(Libs.Misc.okHttpBom))
    implementation(Libs.Misc.okHttp)
    debugImplementation(Libs.Misc.okHttpLoggingInterceptor)
    implementation(Libs.Misc.retrofit)
    implementation(Libs.Misc.retrofitKotlinSerialization)
    implementation(Libs.Misc.kotlinxJsonSerialization)

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation("androidx.test.ext:junit:1.1.2")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
}
