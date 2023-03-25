/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import org.gradle.api.provider.Property

interface BuildConfigExtension {
    val baseUrl: Property<String>
    val debugBaseUrls: Property<String>
    val buildYear: Property<String>
    val githubReposUrls: Property<String>
}
