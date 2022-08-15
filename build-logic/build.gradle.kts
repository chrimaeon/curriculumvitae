/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.benManesVersionsGradle)
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap").any { qualifier ->
            """(?i).*[.-]?$qualifier[.\d-]*""".toRegex()
                .containsMatchIn(candidate.version)
        }
    }
}
