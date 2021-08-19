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

import com.cmgapps.gradle.GenerateBuildConfig
import java.time.LocalDate
import kotlin.io.path.ExperimentalPathApi
import kotlin.io.path.div

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization") version kotlinVersion
    id("com.squareup.sqldelight")
    ktlint
}

val generatedFilesDir: Provider<Directory> = project.layout.buildDirectory.dir("generated")

val generateBuildConfig by tasks.registering(GenerateBuildConfig::class) {
    outputDir.set(generatedFilesDir)

    val baseUrl by configProperties()
    val debugBaseUrls by configProperties()

    this.baseUrl.set(baseUrl)
    this.debugBaseUrls.set(debugBaseUrls)
    this.buildYear.set(LocalDate.now().year.toString())
}

kotlin {
    jvm {
        testRuns["test"].executionTask.configure {
            useJUnitPlatform()
            testLogging {
                events("passed", "skipped", "failed")
            }
        }
    }

    js(IR) {
        useCommonJs()
        browser()
    }

    sourceSets {
        named("commonMain") {
            this.kotlin.srcDir(generatedFilesDir)
            dependencies {
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.bundles.ktor.common)
                implementation(libs.koin)
                implementation(libs.sqldelight.coroutines)
            }
        }

        named("commonTest") {
            dependencies {
                implementation(kotlin("test"))
            }
        }

        named("jvmMain") {
            dependencies {
                implementation(libs.sqldelight.driver.jvm)
            }
        }

        named("jsMain") {
            dependencies {
                implementation(libs.sqldelight.driver.js)
            }
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                freeCompilerArgs = freeCompilerArgs + listOf("-Xopt-in=kotlin.RequiresOptIn")
            }

            compileKotlinTaskProvider {
                dependsOn(generateBuildConfig)
            }
        }
    }
}

@OptIn(ExperimentalPathApi::class)
sqldelight {
    database("CvDatabase") {
        packageName = "com.cmgapps.common.curriculumvitae.data.db"
        schemaOutputDirectory =
            file(projectDir.toPath() / "src" / "commonMain" / "sqldelight" / "databases")
        verifyMigrations = true
    }
}
