/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.VersionConstraint

val Project.libs: VersionCatalog
    get() = extensions.getByType(VersionCatalogsExtension::class.java).named("libs")

fun VersionCatalog.getVersion(name: String): String {
    return this.findVersion(name)
        .orElseThrow { NoSuchElementException("Version for '$name' not found!") }
        .get()
}

fun VersionConstraint.get(): String {
    val requiredVersion = this.requiredVersion
    if (requiredVersion.isNotEmpty()) {
        return requiredVersion
    }
    val strictVersion = this.strictVersion
    return strictVersion.ifEmpty { this.preferredVersion }
}
