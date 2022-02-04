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

import com.cmgapps.gradle.GitVersionTask
import com.cmgapps.gradle.ManifestTransformerTask
import com.cmgapps.gradle.baseConfig
import java.util.Properties

plugins {
    id("com.android.application")
    kotlin("android")
    ktlint
}

android {
    baseConfig(project)

    defaultConfig {
        applicationId = "com.cmgapps.wear.curriculumvitae"
        minSdk = androidWearMinSdkVersion
        targetSdk = androidTargetSdkVersion
        val androidWearableVersion by versionProperties()
        val versionName by versionProperties()
        versionCode = androidWearableVersion.toInt()
        this.versionName = versionName

        resourceConfigurations += listOf("en", "de")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val keystoreDir = rootDir.resolve("keystore")
    val keystorePropsFile = keystoreDir.resolve("curriculumvitae.keystore.properties")

    val releaseSigningConfig = if (keystorePropsFile.exists()) {
        signingConfigs.register("release") {
            val keyProps = Properties().apply {
                keystorePropsFile.inputStream().use {
                    load(it)
                }
            }
            storeFile = keystoreDir.resolve("upload.jks")
            storePassword = keyProps.getProperty("storePass")
            keyAlias = keyProps.getProperty("alias")
            keyPassword = keyProps.getProperty("pass")
        }
    } else null

    val debugSigningConfig = signingConfigs.named("debug") {
        storeFile = keystoreDir.resolve("debug.keystore")
    }

    buildTypes {
        release {
            signingConfig = releaseSigningConfig?.get() ?: debugSigningConfig.get()
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

    packagingOptions {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val gitVersion by tasks.registering(GitVersionTask::class) {
            gitVersionOutputFile.set(
                project.buildDir.resolve("intermediates").resolve("git").resolve("output")
            )
            outputs.upToDateWhen { false }
        }

        val manifestUpdater =
            tasks.register<ManifestTransformerTask>("${variant.name}ManifestUpdater") {
                gitInfoFile.set(gitVersion.flatMap(GitVersionTask::gitVersionOutputFile))
                initialVersionCode = android.defaultConfig.versionCode
                    ?: ManifestTransformerTask.VERSION_CODE_NOT_SET
            }

        variant.artifacts.use(manifestUpdater)
            .wiredWithFiles(
                ManifestTransformerTask::androidManifest,
                ManifestTransformerTask::updatedManifest
            )
            .toTransform(com.android.build.api.artifact.SingleArtifact.MERGED_MANIFEST)
    }
}

@Suppress("UnstableApiUsage")
dependencies {
    implementation(projects.common)
    implementation(projects.commonCompose)
    implementation(libs.playservices.wearable)
    implementation(libs.bundles.compose.wear)
    implementation(libs.koin.compose)
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.okHttp.bom)
    implementation(platform(libs.okHttp.bom))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
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
