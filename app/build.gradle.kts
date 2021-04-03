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

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"

        val baseUrl by apiProperties()

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
}

configurations.all {
    resolutionStrategy {
        force(Libs.Testing.hamcrest)
        dependencySubstitution {
            substitute(module("org.hamcrest:hamcrest-core")).with(module(Libs.Testing.hamcrest))
            substitute(module("org.hamcrest:hamcrest-integration")).with(module(Libs.Testing.hamcrest))
            substitute(module("org.hamcrest:hamcrest-library")).with(module(Libs.Testing.hamcrest))
        }
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
    implementation(project(":shared"))
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.lifecycleLivedataKtx)

    implementation(Libs.AndroidX.composeUi)
    implementation(Libs.AndroidX.composeUiTooling)
    implementation(Libs.AndroidX.composeFoundation)
    implementation(Libs.AndroidX.composeMaterial)
    implementation(Libs.AndroidX.composeMaterialIconsExtended)
    implementation(Libs.AndroidX.composeActivity)
    implementation(Libs.AndroidX.composeViewModel)
    implementation(Libs.AndroidX.composeConstraintLayout)
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

    testImplementation(platform(Libs.Testing.junitBom))
    testImplementation(Libs.Testing.junitJupiter)
    testImplementation(Libs.Testing.androidCoreTesting)
    testImplementation(Libs.Testing.mockito)
    testImplementation(Libs.Testing.coroutineTest)
    testImplementation(Libs.Testing.hamcrest)

    androidTestImplementation(Libs.Testing.extJunit)
    androidTestImplementation(Libs.Testing.archCoreTesting)
    androidTestImplementation(Libs.Testing.espresso)
    androidTestImplementation(Libs.Testing.hamcrest)
    androidTestImplementation(Libs.Testing.retrofitMockServer)
    androidTestImplementation(Libs.Testing.hamcrest)


    androidTestImplementation(Libs.Testing.composeUiTest)

    androidTestImplementation(Libs.Testing.hiltTesting)
    kaptAndroidTest(Libs.Misc.hiltCompiler)
}
