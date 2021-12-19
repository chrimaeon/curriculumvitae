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

import com.cmgapps.common.curriculumvitae.data.network.Address
import com.cmgapps.common.curriculumvitae.data.network.Profile
import com.cmgapps.common.curriculumvitae.data.network.Skill
import kotlinx.datetime.LocalDate
import java.time.Month
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment

fun StubNetworkProfile() = Profile(
    name = "Firstname Lastname",
    address = Address(
        city = "Graz",
        street = "Street 1234",
        postalCode = "8010",
    ),
    email = "me@home.at",
    intro = listOf("Line 1", "Line 2"),
    phone = "+12345678",
    profileImageUrl = "http://image.undefined.com/image.jpeg",
)

fun StubNetworkEmployment() = NetworkEmployment(
    1,
    "Job Title",
    "Employer",
    LocalDate(2021, Month.SEPTEMBER, 2),
    null,
    "City",
    listOf("Description 1", "Description 2")
)

fun StubNetworkSkill() = Skill(
    name = "Skill 1",
    level = 5
)
