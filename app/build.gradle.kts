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

@file:Suppress("UnstableApiUsage")

import com.android.build.api.artifact.SingleArtifact
import com.cmgapps.gradle.GitVersionTask
import com.cmgapps.gradle.ManifestTransformerTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div
import buildToolsVersion as depsBuildToolsVersion

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    alias(libs.plugins.kotlinx.serialization)
    ktlint
    id("dagger.hilt.android.plugin")
    id("com.squareup.wire")
    alias(libs.plugins.licenses)
    alias(libs.plugins.ksp)
}

@OptIn(ExperimentalPathApi::class)
val xorDirPath = buildDir.toPath() / "generated" / "source" / "xor"

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = depsBuildToolsVersion

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        minSdk = androidMinSdkVersion
        targetSdk = androidTargetSdkVersion
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"
        resourceConfigurations += listOf("en", "de")
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
        shaders = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    val keystoreDir = projectDir.resolve("keystore")
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

    @OptIn(ExperimentalPathApi::class)
    val debugSigningConfig = signingConfigs.named("debug") {
        storeFile = keystoreDir.resolve("debug.keystore")
    }

    buildTypes {
        debug {

            buildFeatures {
                viewBinding = true
            }

            val debugBaseUrls by configProperties()
            buildConfigField(
                "String[]",
                "DEBUG_BASE_URLS",
                debugBaseUrls.split(",")
                    .joinToString(prefix = "{", postfix = "}") { """"$it"""" }
            )
        }

        release {
            signingConfig = releaseSigningConfig?.get() ?: debugSigningConfig.get()
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String[]", "DEBUG_BASE_URLS", """{}""")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    sourceSets {
        named("main") {
            java.srcDir(xorDirPath)
        }

        listOf("debug", "release").map { named(it) }.forEach { sourceSet ->
            sourceSet {
                java {
                    listOf("kotlin", "java").forEach {
                        @OptIn(ExperimentalPathApi::class)
                        srcDir(buildDir.toPath() / "generated" / "ksp" / sourceSet.name / it)
                    }
                }
            }
        }

        listOf("test", "androidTest").map { named(it) }.forEach { sourceSet ->
            sourceSet {
                java.srcDir(
                    project.projectDir.resolve("src").resolve("sharedTest").resolve("java")
                )
            }
        }
    }

    testOptions {
        unitTests.all { test ->
            test.useJUnitPlatform()
            test.testLogging {
                events("passed", "skipped", "failed")
            }
            test.afterSuite(testCompletionLog())
        }
    }

    packagingOptions {
        resources.excludes += setOf("META-INF/AL2.0", "META-INF/LGPL2.1")
    }

    lint {
        // disable Timber Lint; see https://github.com/JakeWharton/timber/issues/408
        disable(
            "LogNotTimber",
            "StringFormatInTimber",
            "ThrowableNotAtBeginning",
            "BinaryOperationInTimber",
            "TimberArgCount",
            "TimberArgTypes",
            "TimberTagLength",
            "TimberExceptionLogging"
        )
    }

    applicationVariants.all {
        val variantName = name
        val licenseTask = tasks.named("license${variantName.capitalize()}Report")

        val copyTask = tasks.register<Copy>("copy${licenseTask.name}") {
            from(licenseTask)
            into(android.sourceSets.getByName(variantName).assets.srcDirs.single())
        }

        mergeAssetsProvider {
            dependsOn(copyTask)
        }
    }
}

// class HtmlLicenseAsset(private val fileName: String) :
//     com.android.build.api.artifact.Artifact.Single<RegularFile>(
//         com.android.build.api.artifact.ArtifactKind.FILE,
//         Category.INTERMEDIATES,
//     ),
//     com.android.build.api.artifact.Artifact.Replaceable {
//     override fun getFolderName(): String {
//         return "merged_assets"
//     }
//
//     override fun getFileSystemLocationName(): String {
//         return fileName
//     }
// }

androidComponents {
    // onVariants { variant ->
    // // TODO does not work correctly -> task is not triggered
    // val copyLicenseAsset =
    //     tasks.register<CopyLicenseAssetTask>("copyLicense${variant.name.capitalize()}Asset") {
    //         val licenseReportTask =
    //             tasks.named<LicensesTask>("license${variant.name.capitalize()}Report")
    //         licenseFile.set(licenseReportTask.flatMap { it.reports.html.destination })
    //         dependsOn(licenseReportTask)
    //     }
    //
    // variant.artifacts.use(copyLicenseAsset)
    //     .wiredWith(CopyLicenseAssetTask::output)
    //     .toCreate(HtmlLicenseAsset("license.html"))
    // }

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
            }

        variant.artifacts.use(manifestUpdater)
            .wiredWithFiles(
                ManifestTransformerTask::androidManifest,
                ManifestTransformerTask::updatedManifest
            )
            .toTransform(SingleArtifact.MERGED_MANIFEST)
    }
}

wire {
    kotlin { }
}

licenses {
    additionalProjects(":common", ":wearable")
    reports {
        html.enabled = true
    }
}

tasks {
    val generateEmailAddress by registering {
        val outputDir = xorDirPath

        val email by configProperties()
        inputs.property("email", email)
        val packageName = android.defaultConfig.applicationId ?: error("app id not set")
        inputs.property("packageName", packageName)

        outputs.dir(outputDir)

        doLast {
            generateEmailAddress(email, packageName, outputDir.toFile())
        }
    }

    withType<KotlinCompile> {
        dependsOn(generateEmailAddress)
    }
}

@Suppress("UnstableApiUsage")
dependencies {
    implementation(projects.common)
    implementation(projects.commonCompose)

    implementation(libs.bundles.androidx)

    implementation(libs.bundles.compose.app)

    implementation(libs.bundles.accompanist)

    implementation(libs.timber)

    implementation(libs.logtag.logTag)
    ksp(libs.logtag.processor)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(platform(libs.okHttp.bom))
    implementation("com.squareup.okhttp3:okhttp")
    debugImplementation("com.squareup.okhttp3:logging-interceptor")
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.cmgapps.lintLogDebug)
    implementation(libs.coil.compose)
    implementation(libs.collapsingToolbar)
    implementation(libs.dropbox.store)
    implementation(libs.bundles.ktor.android)
    implementation(libs.sqldelight.driver.android)
    implementation(libs.sqldelight.coroutines)

    debugImplementation(libs.processPhoenix)
    debugImplementation(libs.leakCanary)
    debugImplementation(libs.androidx.preference)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.androidx.coreTesting)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutinesTest)
    testImplementation(libs.hamcrest)
    testImplementation(libs.turbine)
    testImplementation(libs.sqldelight.driver.jvm)

    androidTestImplementation(libs.androidx.extJunit)
    androidTestImplementation(libs.androidx.coreTesting)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.ktor.client.mock)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.compose.uiTest)
    androidTestImplementation(libs.sqldelight.driver.android)

    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)
}
