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

import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    kotlin("plugin.serialization") version kotlinVersion
    ktlint
    id("dagger.hilt.android.plugin")
    id("com.google.protobuf") version protobufPluginVersion
    // `jacoco-convention`

    // jacoco
}

@OptIn(ExperimentalPathApi::class)
val xorDirPath = buildDir.toPath() / "generated" / "source" / "xor"

android {
    compileSdk = 30
    buildToolsVersion = "30.0.3"

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        minSdk = 26
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"

        val baseUrl by configProperties()

        resourceConfigurations.addAll(listOf("en", "de"))

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

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:" + libs.versions.protobuf.get()
    }

    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
            }
        }
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

// // Share sources folder with other projects for aggregated JaCoCo reports
// configurations.create("transitiveSourcesElements") {
//     isVisible = false
//     isCanBeResolved = false
//     isCanBeConsumed = true
//     attributes {
//         attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
//         attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
//         attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
//     }
//
//     (android.sourceSets.named("main").get().java.srcDirs + android.sourceSets.named("debug")
//         .get().java.srcDirs).forEach {
//             outgoing.artifact(it)
//     }
// }
//
// // Share sources folder with other projects for aggregated JaCoCo reports
// configurations.create("transitiveClassesElements") {
//     isVisible = false
//     isCanBeResolved = false
//     isCanBeConsumed = true
//     attributes {
//         attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
//         attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
//         attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("classes-folder"))
//     }
//
//     configure<SourceSetContainer> {
//         outgoing.artifact(buildDir.resolve("intermediates/javac/debug/classes"))
//         outgoing.artifact(buildDir.resolve("tmp/kotlin-classes/debug"))
//     }
// }
//
// // Share the coverage data to be aggregated for the whole product
// configurations.create("coverageDataElements") {
//     isVisible = false
//     isCanBeResolved = false
//     isCanBeConsumed = true
//     attributes {
//         attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
//         attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
//         attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
//     }
//
//     afterEvaluate {
//         outgoing.artifact(tasks.named("testDebugUnitTest").map { task ->
//             task.extensions.getByType<JacocoTaskExtension>().destinationFile!!
//         })
//     }
// }

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
    implementation(libs.protobuf.javalite)

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
