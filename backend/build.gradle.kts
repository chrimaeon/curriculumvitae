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
    implementation(libs.bundles.logback)
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
