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

package com.cmgapps.wear.curriculumvitae

import com.cmgapps.common.curriculumvitae.data.network.Address
import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.cmgapps.common.curriculumvitae.data.network.Profile
import com.cmgapps.common.curriculumvitae.data.network.Skill
import kotlinx.datetime.LocalDate
import java.time.Month

fun StubNetworkProfile(): Profile = Profile(
    name = "First Last",
    address = Address(
        city = "Graz",
        street = "Street 1234",
        postalCode = "8010",
    ),
    email = "me@home.at",
    intro = listOf("Line 1", "Line 2"),
    phone = "+12345678",
    profileImagePath = "/image.jpeg",
)

fun StubNetworkEmployment(): Employment = Employment(
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

fun StubNetworkSkill(): Skill = Skill("Skill level 1", 1)