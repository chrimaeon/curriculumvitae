/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")
@file:OptIn(kotlin.io.path.ExperimentalPathApi::class)

import com.android.build.api.artifact.SingleArtifact
import com.android.build.gradle.tasks.ExternalNativeBuildTask
import com.cmgapps.gradle.curriculumvitae.GenerateJniDataTask
import com.cmgapps.gradle.curriculumvitae.GitVersionTask
import com.cmgapps.gradle.curriculumvitae.ManifestTransformerTask
import com.cmgapps.gradle.curriculumvitae.ObfuscateEmailTask
import com.cmgapps.gradle.curriculumvitae.configProperty
import com.cmgapps.gradle.curriculumvitae.versionProperty
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.util.Properties
import kotlin.io.path.div

plugins {
    id("curriculumvitae.android.application")
    kotlin("kapt")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlinx.serialization)
    id("ktlint")
    id("dagger.hilt.android.plugin")
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.wire)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.licenses)
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.ksp)
}

val xorDirPath = buildDir.toPath() / "generated" / "source" / "xor"

android {
    ndkVersion = libs.versions.androidNdk.get()
    namespace = "com.cmgapps.android.curriculumvitae"

    defaultConfig {
        applicationId = "com.cmgapps.android.curriculumvitae"
        val androidAppVersion by versionProperty
        val versionName by versionProperty
        versionCode = androidAppVersion.toInt()
        this.versionName = versionName

        testInstrumentationRunner = "com.cmgapps.android.curriculumvitae.CvTestRunner"
        resourceConfigurations += listOf("en", "de")

        externalNativeBuild {
            cmake {
                cFlags("-Wall")
                cppFlags("-Wall")
                arguments("-DANDROID_STL=c++_static")
            }
        }
    }

    buildFeatures {
        compose = true
        aidl = false
        renderScript = false
        shaders = false
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
        named("main") {
            java.srcDir(xorDirPath)
        }

        named("benchmark") {
            java {
                srcDirs(sourceSets["release"].java.srcDirs)
            }
            res {
                srcDirs(sourceSets["release"].res.srcDirs)
            }
        }

        listOf("debug", "release").map { named(it) }.forEach { sourceSet ->
            sourceSet {
                java {
                    listOf("kotlin", "java").forEach {
                        // srcDir((buildDir.toPath() / "generated" / "ksp" / sourceSet.name / it).toFile())
                    }
                }
            }
        }

        listOf("test", "androidTest").map { named(it) }.forEach { sourceSet ->
            sourceSet {
                java.srcDir(
                    project.projectDir.toPath() / "src" / "sharedTest" / "java",
                )
            }
        }
    }

    packagingOptions {
        resources.excludes.add("/META-INF/{AL2.0,LGPL2.1}")
    }

    applicationVariants.all {
        val variantName = name
        val licenseTask = tasks.named("license${variantName.capitalize()}Report")

        val copyTask = tasks.register<Copy>("copy${licenseTask.name}") {
            from(licenseTask)
            into(android.sourceSets[variantName].assets.srcDirs.first())
        }

        mergeAssetsProvider {
            dependsOn(copyTask)
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

tasks {

    val obfuscateEmailAddress by registering(ObfuscateEmailTask::class) {
        outputDir.set(xorDirPath.toFile())
        emailAddress.set(
            provider {
                val email by configProperty
                email
            },
        )

        packageName.set(android.defaultConfig.applicationId ?: error("app id not set"))
    }

    withType<KotlinCompile> {
        dependsOn(obfuscateEmailAddress)
    }

    val generateJniData by registering(GenerateJniDataTask::class) {
        source.set((projectDir.toPath() / "src" / "main" / "jni" / "names.json").toFile())
        outputFile.set((buildDir.toPath() / "generated" / "jni" / "encodedNames.h").toFile())
    }

    withType<ExternalNativeBuildTask> {
        dependsOn(generateJniData)
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
}
