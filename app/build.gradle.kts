
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

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version kotlinVersion
    ktlint
    id("dagger.hilt.android.plugin")
}

val xorDirPath = buildDir.resolve("generated/source/xor")

android {
    compileSdkVersion(30)
    buildToolsVersion("30.0.3")

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        minSdkVersion(26)
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"

        val baseUrl by configProperties()

        resConfigs("en", "de")

        buildConfigField("String", "BASE_URL", """"$baseUrl"""")

        javaCompileOptions {
            annotationProcessorOptions {
                arguments += mapOf(
                    "room.schemaLocation" to projectDir.resolve("schemas").absolutePath,
                    "room.incremental" to "true",
                )
            }
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
            java.srcDir(xorDirPath)
        }

        val sharedTestDir = project.projectDir.resolve("src").resolve("sharedTest")

        named("test") {
            java.srcDir(sharedTestDir.resolve("java"))
        }

        named("androidTest") {
            java.srcDir(sharedTestDir.resolve("java"))

        }
    }

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
    }

    testOptions {
        unitTests.all {
            it.useJUnitPlatform()
            it.testLogging {
                events("passed", "skipped", "failed")
            }
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
            generateEmailAddress(email, packageName, outputDir)
        }
    }

    withType<KotlinCompile> {
        dependsOn(generateEmailAddress)
    }
}

@Suppress("UnstableApiUsage")
dependencies {
    implementation(projects.shared)
    implementation(libs.bundles.androidx)

    implementation(libs.bundles.compose)

    implementation(libs.bundles.accompanist)

    implementation(libs.timber)

    implementation(libs.logtag.logTag)
    kapt(libs.logtag.processor)

    implementation(libs.hilt.android)
    kapt(libs.hilt.compiler)

    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    implementation(platform(libs.okHttp.bom))
    implementation("com.squareup.okhttp3:okhttp")
    debugImplementation("com.squareup.okhttp3:logging-interceptor")
    implementation(libs.retrofit2.retrofit)
    implementation(libs.retrofit2.kotlinxSerialization)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.androidx.coreTesting)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.kotlinx.coroutinesTest)
    testImplementation(libs.hamcrest)

    androidTestImplementation(libs.androidx.extJunit)
    androidTestImplementation(libs.androidx.coreTesting)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.retrofit2.mockServer)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.compose.uiTest)

    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)
}
