/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
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

plugins {
    id("com.android.test")
    kotlin("android")
}

android {
    compileSdk = androidCompileSdkVersion
    buildToolsVersion = buildToolsVersion

    defaultConfig {
        minSdk = androidMinSdkVersion
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
    beforeVariants(selector().all()) {
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
