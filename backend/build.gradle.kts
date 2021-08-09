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

import org.jetbrains.kotlin.utils.addToStdlib.cast
import java.util.Properties

plugins {
    kotlin("jvm")
    id("com.google.cloud.tools.appengine")
    kotlin("plugin.serialization") version kotlinVersion
    id("org.kordamp.gradle.markdown") version "2.2.0"
    ktlint
}

val localImplementation: Configuration by configurations.creating {
    extendsFrom(configurations.implementation.get())
}

val localSourceSet: SourceSet = sourceSets.create("local") {
    java {
        srcDir(projectDir.resolve("src/local/kotlin"))
        compileClasspath += sourceSets.main.get().output + configurations["localCompileClasspath"]
        runtimeClasspath += output + sourceSets.main.get().output + configurations["localRuntimeClasspath"]
    }
}

group = "com.cmgapps.ktor"
version = "alpha1"

appengine {
    tools {
        val localPropsFile = rootDir.resolve("local.properties")
        if (localPropsFile.exists()) {
            val localProperties = Properties().apply {
                localPropsFile.inputStream().use {
                    load(it)
                }
            }
            setCloudSdkHome(localProperties["gcloud.sdk.dir"])
        }
    }

    val project: Project = project

    deploy {
        projectId = "GCLOUD_CONFIG"
        version = project.version.cast()
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
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

    register<JavaExec>("run") {
        classpath =
            sourceSets.main.get().runtimeClasspath + localSourceSet.runtimeClasspath
        mainClass.set("MainKt")
        jvmArgs("-Dio.ktor.development=true")
    }
}

@Suppress("UnstableApiUsage")
dependencies {
    implementation(projects.common)
    implementation(kotlin("stdlib-jdk8", libs.versions.kotlin.get()))

    localImplementation(libs.ktor.netty)

    implementation(libs.bundles.ktor.server)
    implementation(libs.kotlinCss)
    implementation(libs.bundles.logback)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.kotlinx.datetime)

    "providedCompile"(libs.appEngine)

    testImplementation(libs.ktor.testing)
    testImplementation(platform(libs.junit.bom))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation(libs.hamcrest)
    testImplementation(libs.mockito.jupiter)
}
