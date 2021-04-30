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

package com.cmgapps.gradle

plugins {
    java
    jacoco
}

// A resolvable configuration to collect source code
val sourcesPath: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("source-folders"))
    }
}

val classesPath: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
        attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named("classes-folder"))
    }
}

// A resolvable configuration to collect JaCoCo coverage data
val coverageDataPath: Configuration by configurations.creating {
    isVisible = false
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations.implementation.get())
    attributes {
        attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
        attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named("jacoco-coverage-data"))
    }
}

// Task to gather code coverage from multiple subprojects
val codeCoverageReport by tasks.registering(JacocoReport::class) {
    val classFileCollection = classesPath.incoming.artifactView { lenient(true) }.files
    additionalClassDirs(classFileCollection)
    val sourceFileCollection = sourcesPath.incoming.artifactView { lenient(true) }.files
    additionalSourceDirs(sourceFileCollection)
    val coverageFileCollection = coverageDataPath.incoming.artifactView { lenient(true) }.files.filter { it.exists() }
    executionData(coverageFileCollection)

    reports {
        xml.isEnabled = true
        html.isEnabled = true
    }

    doFirst {
        logger.lifecycle("Generating coverage report")
        val separator = "\t\t\n"
        logger.lifecycle("\tfor Classes:\n" + classFileCollection.joinToString(separator))
        logger.lifecycle("\tfor Sources:\n" + sourceFileCollection.joinToString(separator))
        logger.lifecycle("with execution data: " + coverageFileCollection.joinToString(separator))
    }
}

// Make JaCoCo report generation part of the 'check' lifecycle phase
tasks.check {
    dependsOn(codeCoverageReport)
}
