/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("UnstableApiUsage")

import java.util.Properties

plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.appengine")
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.markdown)
    ktlint
    application
    alias(libs.plugins.shadowJar)
}

group = "com.cmgapps.ktor"
val versionName by versionProperties()
version = versionName

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true")
}

appengine {
    val localPropsFile = rootDir.resolve("local.properties")
    val localProperties = if (localPropsFile.exists()) {
        Properties().apply {
            localPropsFile.inputStream().use {
                load(it)
            }
        }
    } else null

    tools {
        if (localProperties?.containsKey("gcloud.sdk.dir") == true) {
            setCloudSdkHome(localProperties["gcloud.sdk.dir"])
        }
    }

    stage {
        setArtifact(
            provider {
                tasks.shadowJar.get().outputs.files.singleFile
            }
        )
    }

    deploy {
        if (localProperties?.containsKey("gcloud.project.id") == true) {
            projectId = localProperties.getProperty("gcloud.project.id")
        }
        val backendVersion by versionProperties()
        version = backendVersion
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

tasks {
    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
    }

    htmlToMarkdown {
        sourceDir = sourceSets.test.get().resources.sourceDirectories.singleFile
        configuration = mapOf("tables" to true)
        doLast {
            copy {
                from(outputDir)
                include("root.md")
                into(projectDir)
                rename {
                    "README.md"
                }
            }
        }
    }
}

dependencies {
    implementation(projects.common)
    implementation(kotlin("stdlib-jdk8", libs.versions.kotlin.get()))
    implementation(libs.ktor.netty)

    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinCss)
    implementation(libs.logback.classic)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)
    implementation(libs.bundles.koin.server)
    implementation(libs.sqldelight.driver.jvm)

    testImplementation(libs.ktor.testing)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.mockito.kotlin)
}
