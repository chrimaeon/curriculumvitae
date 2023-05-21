/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import com.cmgapps.gradle.curriculumvitae.GitVersionTask
import com.cmgapps.gradle.curriculumvitae.ManifestTransformerTask
import java.util.Properties

plugins {
    id("curriculumvitae.android.application")
    id("ktlint")
}

android {
    namespace = "com.cmgapps.wear.curriculumvitae"
    defaultConfig {
        applicationId = "com.cmgapps.wear.curriculumvitae"
        minSdk = libs.versions.androidWearMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = libs.versions.androidWearableVersion.get().toInt()
        this.versionName = libs.versions.versionName.get()

        resourceConfigurations += listOf("en", "de")

        testInstrumentationRunner = "com.cmgapps.wear.curriculumvitae.test.CvTestRunner"
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
    } else {
        null
    }

    val debugSigningConfig = signingConfigs.named("debug") {
        storeFile = keystoreDir.resolve("debug.keystore")
    }

    buildTypes {
        release {
            signingConfig = releaseSigningConfig?.get() ?: debugSigningConfig.get()
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    buildFeatures {
        compose = true
        shaders = false
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.composeCompiler.get()
    }

    packaging {
        resources.excludes += setOf("/META-INF/{AL2.0,LGPL2.1}")
    }
}

androidComponents {
    onVariants(selector().withBuildType("release")) { variant ->
        val gitVersion by tasks.registering(GitVersionTask::class) {
            gitVersionOutputFile.set(
                project.buildDir.resolve("intermediates").resolve("git").resolve("output"),
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
                ManifestTransformerTask::updatedManifest,
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
    implementation(libs.koin.compose.android)
    implementation(libs.ktor.client.android)
    implementation(libs.coil.compose)
    implementation(libs.okHttp.bom)
    implementation(platform(libs.okHttp.bom))
    implementation("com.squareup.okhttp3:okhttp")
    implementation("com.squareup.okhttp3:logging-interceptor")
    implementation(libs.accompanist.placeholder)
    implementation(libs.accompanist.pagerIndicators)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.androidx.testRunner)
    androidTestImplementation(libs.androidx.extJunit)
    androidTestImplementation(libs.androidx.coreTesting)
    androidTestImplementation(libs.compose.uiTest)
    androidTestImplementation(libs.koin.test)
    androidTestImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.ktor.client.contentNegotiation)
    androidTestImplementation(libs.ktor.serialization)
    androidTestImplementation(libs.sqldelight.driver.android)
}
