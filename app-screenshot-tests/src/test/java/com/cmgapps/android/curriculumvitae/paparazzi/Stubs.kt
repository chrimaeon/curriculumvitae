/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("TestFunctionName")

package com.cmgapps.android.curriculumvitae.paparazzi

import com.cmgapps.android.curriculumvitae.data.datastore.Profile
import com.cmgapps.android.curriculumvitae.data.datastore.asDataStoreModel
import com.cmgapps.common.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.common.curriculumvitae.data.network.asDatabaseModel
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import com.cmgapps.common.curriculumvitae.data.network.Address as NetworkAddress
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile
import com.cmgapps.common.curriculumvitae.data.network.Skill as NetworkSkill

fun StubNetworkProfile() = NetworkProfile(
    name = "Firstname Lastname",
    address = NetworkAddress(
        city = "Graz",
        street = "Street 1234",
        postalCode = "8010",
    ),
    email = "me@home.at",
    intro = listOf("Line 1", "Line 2"),
    phone = "+12345678",
    profileImagePath = "/image.jpeg",
)

fun StubDomainProfile() = StubNetworkProfile().asDomainModel()
fun StubDataStoreProfile(): Profile = StubNetworkProfile().asDataStoreModel()

fun StubNetworkEmployment() = NetworkEmployment(
    id = 1,
    jobTitle = "Developer",
    employer = "My Company",
    startDate = LocalDate(2021, Month.APRIL, 17),
    endDate = null,
    city = "Home City",
    description = listOf(
        "stub description",
    )
)

fun StubDatabaseEmployment() = StubNetworkEmployment().asDatabaseModel()
fun StubDomainEmployment() = StubDatabaseEmployment().asDomainModel()

fun StubNetworkSkills() = listOf(
    NetworkSkill(
        name = "Skill level 1",
        level = 1
    )
)

fun StubDomainSkills() = StubNetworkSkills().asDomainModel()
fun StubDataStoreSkills() = StubNetworkSkills().asDataStoreModel()
