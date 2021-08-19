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

@file:Suppress("TestFunctionName")

package com.cmgapps.android.curriculumvitae.test

import com.cmgapps.android.curriculumvitae.data.datastore.Address
import com.cmgapps.android.curriculumvitae.data.datastore.Profile
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import com.cmgapps.common.curriculumvitae.data.db.Employment as DatabaseEmployment
import com.cmgapps.common.curriculumvitae.data.domain.Address as DomainAddress
import com.cmgapps.common.curriculumvitae.data.domain.Profile as DomainProfile
import com.cmgapps.common.curriculumvitae.data.network.Address as NetworkAddress
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile

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
    profileImageUrl = "http://image.undefined.com/image.jpeg",
)

fun StubDomainProfile() = DomainProfile(
    name = "Firstname Lastname",
    email = "me@home.at",
    intro = listOf("Line 1", "Line 2"),
    phone = "+12345678",
    profileImageUrl = "http://image.undefined.com/image.jpeg",
    address = DomainAddress(
        city = "Graz",
        street = "Street 1",
        postalCode = "8010",
    ),
)

fun StubNetworkEmployment() = NetworkEmployment(
    jobTitle = "Developer",
    employer = "My Company",
    startDate = LocalDate(2021, Month.APRIL, 17),
    endDate = null,
    city = "Home City",
    description = listOf(
        "stub description",
    )
)

private val employmentId = StubNetworkEmployment().hashCode()

fun StubDatabaseEmployment() = DatabaseEmployment(
    id = employmentId,
    job_title = "Developer",
    employer = "My Company",
    start_date = LocalDate(2021, Month.APRIL, 17).toString(),
    end_date = null,
    city = "Home City",
    description = listOf("stub description")
)

fun StubDomainEmployment() = Employment(
    id = employmentId,
    jobTitle = "Developer",
    employer = "My Company",
    startDate = LocalDate(2021, Month.APRIL, 17),
    endDate = null,
    city = "Home City",
    description = listOf("stub description")
)

fun StubDataStoreProfile(): Profile = Profile.newBuilder().apply {
    name = "Firstname Lastname"
    address = Address.newBuilder().apply {
        city = "Graz"
        street = "Street 1"
        postalCode = "8010"
    }.build()
    email = "me@home.at"
    addAllIntro(listOf("Line 1", "Line 2"))
    phone = "+12345678"
    profileImageUrl = "http://image.undefined.com/image.jpeg"
}.build()
