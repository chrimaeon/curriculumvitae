/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.android.build.api.artifact.SingleArtifact
import com.cmgapps.gradle.curriculumvitae.GitVersionTask
import com.cmgapps.gradle.curriculumvitae.ManifestTransformerTask
import com.cmgapps.gradle.curriculumvitae.configProperty
import java.util.Locale
import java.util.Properties

plugins {
    id("curriculumvitae.android.application")
    kotlin("kapt")
    alias(libs.plugins.kotlin.serialization)
    id("ktlint")
    id("dagger.hilt.android.plugin")
    alias(libs.plugins.wire)
    alias(libs.plugins.licenses)
    alias(libs.plugins.ksp)
    id("obfuscateEmail")
    id("generateJniData")
}

val jniSrcDirPath = android.sourceSets["main"].jniLibs.srcDirs.first()

android {
    ndkVersion = libs.versions.androidNdk.get()
    namespace = "com.cmgapps.android.curriculumvitae"

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        versionCode = libs.versions.androidAppVersion.get().toInt()
        versionName = libs.versions.versionName.get()

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"
        resourceConfigurations += listOf("en", "de")

        externalNativeBuild {
            cmake {
                cFlags("-Wall")
                cppFlags("-Wall")
                arguments(
                    "-DANDROID_STL=c++_static",
                    "-DJNI_SRC_DIR=$jniSrcDirPath",
                )
            }
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
        debug {
            buildFeatures {
                viewBinding = true
            }

            val debugBaseUrls by configProperty
            buildConfigField(
                "String[]",
                "DEBUG_BASE_URLS",
                debugBaseUrls.split(",")
                    .joinToString(prefix = "{", postfix = "}") { """"$it"""" },
            )

            externalNativeBuild {
                cmake {
                    cFlags("-DDEBUG")
                    cppFlags("-DDEBUG")
                }
            }

            versionNameSuffix = "-Debug"
        }

        release {
            signingConfig = releaseSigningConfig?.get() ?: debugSigningConfig.get()
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
            buildConfigField("String[]", "DEBUG_BASE_URLS", """{}""")

            externalNativeBuild {
                cmake {
                    cFlags("-DRELEASE")
                    cppFlags("-DRELEASE")
                }
            }
            versionNameSuffix = ""
        }

        register("benchmark") {
            initWith(buildTypes["release"])
            signingConfig = debugSigningConfig.get()
            // see https://issuetracker.google.com/issues/216940881
            proguardFiles += projectDir.resolve("proguard-rules-benchmark.pro")
            externalNativeBuild {
                cmake {
                    cFlags("-DDEBUG")
                    cppFlags("-DDEBUG")
                }
            }
            versionNameSuffix = "-Benchmark"
        }
    }

    sourceSets {
        named("benchmark") {
            java {
                srcDirs(sourceSets["release"].java.srcDirs)
            }
            res {
                srcDirs(sourceSets["release"].res.srcDirs)
            }
        }
    }

    packaging {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }

    applicationVariants.all {
        val variantName = name
        val capitalizedVariantName =
            variantName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        afterEvaluate {
            tasks.named("ksp${capitalizedVariantName}Kotlin") {
                dependsOn("generate${capitalizedVariantName}Protos")
            }
        }
    }

    externalNativeBuild {
        cmake {
            path = projectDir.resolve("CMakeLists.txt")
            version = "3.22.1"
        }
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

obfuscateEmail {
    emailAddress.set(
        provider {
            val email by configProperty
            email
        },
    )
}

generateJniData {
    source.set(jniSrcDirPath.resolve("names.json"))
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
    implementation(libs.dropbox.store)
    implementation(libs.bundles.ktor.android)
    implementation(libs.sqldelight.driver.android)
    implementation(libs.sqldelight.coroutines)
    implementation(libs.androidx.profileInstaller)
    implementation(libs.androidx.webkit)

    debugImplementation(libs.processPhoenix)
    debugImplementation(libs.leakCanary)
    debugImplementation(libs.androidx.preference)
    debugImplementation(libs.compose.uiTestManifest)

    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.androidx.coreTesting)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.kotlinx.coroutinesTest)
    testImplementation(libs.hamcrest)
    testImplementation(libs.turbine)
    testImplementation(libs.sqldelight.driver.jvm)

    androidTestImplementation(libs.junit.junit)
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

    testImplementation(projects.androidSharedTest)
    androidTestImplementation(projects.androidSharedTest)
}
