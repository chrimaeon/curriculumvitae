/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.infra.model

data class ApiInfo(
    val version: String,
    val contactName: String,
    val contactEmail: String,
    val serverUrl: String,
    val serverDescription: String,
)
