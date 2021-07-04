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

import com.android.build.api.artifact.SingleArtifact
import com.cmgapps.gradle.CopyLicenseAssetTask
import com.cmgapps.gradle.GitVersionTask
import com.cmgapps.gradle.ManifestTransformerTask
import com.cmgapps.license.LicensesTask
import com.google.protobuf.gradle.generateProtoTasks
import com.google.protobuf.gradle.id
import com.google.protobuf.gradle.protobuf
import com.google.protobuf.gradle.protoc
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.LocalDate
import java.util.Properties
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
    id("com.cmgapps.licenses") version licensesVersion
    id("com.google.devtools.ksp") version "1.5.10-1.0.0-beta02"
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
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    val releaseSigningConfig = if (!isCi()) {
        signingConfigs.register("release") {
            val keystoreDir = projectDir.resolve("keystore")
            val keyProps = Properties().apply {
                keystoreDir.resolve("curriculumvitae.keystore.properties").inputStream().use {
                    load(it)
                }
            }
            storeFile = keystoreDir.resolve("upload.jks")
            storePassword = keyProps.getProperty("storePass")
            keyAlias = keyProps.getProperty("alias")
            keyPassword = keyProps.getProperty("pass")
        }
    } else null

    buildTypes {
        debug {
            buildConfigField("String", "BUILD_YEAR", "\"DEBUG\"")
        }

        release {
            signingConfig = releaseSigningConfig?.get()
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BUILD_YEAR", "\"${LocalDate.now().year}\"")
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

    kotlinOptions {
        freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
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
}

kapt {
    correctErrorTypes = true
    arguments {
        arg("room.schemaLocation", projectDir.resolve("schemas").absolutePath)
        arg("room.incremental", "true")
    }
}

class HtmlLicenseAsset(private val fileName: String) :
    com.android.build.api.artifact.Artifact.Single<RegularFile>(
        com.android.build.api.artifact.ArtifactKind.FILE,
        Category.INTERMEDIATES,
    ),
    com.android.build.api.artifact.Artifact.Replaceable {
    override fun getFolderName(): String {
        return "merged_assets"
    }

    override fun getFileSystemLocationName(): String {
        return fileName
    }
}

androidComponents {
    onVariants { variant ->
        // TODO does not work correctly -> task is not triggered
        val copyLicenseAsset =
            tasks.register<CopyLicenseAssetTask>("copyLicense${variant.name.capitalize()}Asset") {
                val licenseReportTask =
                    tasks.named<LicensesTask>("license${variant.name.capitalize()}Report")
                licenseFile.set(licenseReportTask.flatMap { it.reports.html.destination })
                dependsOn(licenseReportTask)
            }

        variant.artifacts.use(copyLicenseAsset)
            .wiredWith(CopyLicenseAssetTask::output)
            .toCreate(HtmlLicenseAsset("license.html"))
    }

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

licenses {
    additionalProjects(":shared")
    reports {
        html.enabled.set(true)
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
    implementation(projects.shared)
    implementation(libs.bundles.androidx)

    implementation(libs.bundles.compose)

    implementation(libs.bundles.accompanist)

    implementation(libs.timber)

    implementation(libs.logtag.logTag)
    ksp(libs.logtag.processor)

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
    implementation(libs.cmgapps.lintLogDebug)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.androidx.coreTesting)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.kotlinx.coroutinesTest)
    testImplementation(libs.hamcrest)
    testImplementation(libs.turbine)

    androidTestImplementation(libs.androidx.extJunit)
    androidTestImplementation(libs.androidx.coreTesting)
    androidTestImplementation(libs.androidx.espresso)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.retrofit2.mockServer)
    androidTestImplementation(libs.hamcrest)
    androidTestImplementation(libs.compose.uiTest)

    androidTestImplementation(libs.hilt.testing)
    kaptAndroidTest(libs.hilt.compiler)
}
