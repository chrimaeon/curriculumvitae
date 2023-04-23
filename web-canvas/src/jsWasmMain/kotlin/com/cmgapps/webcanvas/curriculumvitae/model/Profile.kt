/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.webcanvas.curriculumvitae.model

/**
 * TODO placeholder until `:common` can be shared
 */
data class Profile(
    val name: String,
    val phone: String,
    val profileImagePath: String,
    val address: Address,
    val email: String,
    val intro: List<String>,
)

/**
 * TODO placeholder until `:common` can be shared
 */
data class Address(val street: String, val city: String, val postalCode: String)
