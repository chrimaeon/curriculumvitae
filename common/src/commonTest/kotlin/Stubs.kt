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

import com.cmgapps.common.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.common.curriculumvitae.data.network.asDatabaseModel
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import com.cmgapps.common.curriculumvitae.data.db.Employment as DatabaseEmployment
import com.cmgapps.common.curriculumvitae.data.domain.Employment as DomainEmployment
import com.cmgapps.common.curriculumvitae.data.domain.OssProject as DomainOssProject
import com.cmgapps.common.curriculumvitae.data.domain.Profile as DomainProfile
import com.cmgapps.common.curriculumvitae.data.domain.Skill as DomainSkill
import com.cmgapps.common.curriculumvitae.data.domain.Status as DomainStatus
import com.cmgapps.common.curriculumvitae.data.network.Address as NetworkAddress
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment
import com.cmgapps.common.curriculumvitae.data.network.OssProject as NetworkOssProject
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile
import com.cmgapps.common.curriculumvitae.data.network.Skill as NetworkSkill
import com.cmgapps.common.curriculumvitae.data.network.Status as NetworkStatus

fun StubNetworkProfile(): NetworkProfile = NetworkProfile(
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

fun StubDomainProfile(): DomainProfile = StubNetworkProfile().asDomainModel()

fun StubNetworkEmployment(): NetworkEmployment = NetworkEmployment(
    id = 1,
    jobTitle = "Developer",
    employer = "My Company",
    startDate = LocalDate(2021, Month.APRIL, 17),
    endDate = null,
    city = "Home City",
    description = listOf(
        "stub description",
    ),
)

fun StubDatabaseEmployment(): DatabaseEmployment = StubNetworkEmployment().asDatabaseModel()
fun StubDomainEmployment(): DomainEmployment = StubDatabaseEmployment().asDomainModel()

fun StubNetworkStatus(): NetworkStatus = NetworkStatus(1, 2, 3, 4)
fun StubDomainStatus(): DomainStatus = StubNetworkStatus().asDomainModel()

fun StubNetworkSkill(): NetworkSkill = NetworkSkill("Skill level 1", 1)
fun StubDomainSkill(): DomainSkill = with(StubNetworkSkill()) { DomainSkill(name, level) }

fun StubNetworkOssProject(): NetworkOssProject = NetworkOssProject(
    "my-project",
    "description",
    "url",
    listOf("Topic1", "Tobic2"),
    4,
    false,
    false,
    false,
    Instant.parse("1979-09-02T09:00:00Z"),
)

fun StubDomainOssProject() = with(StubNetworkOssProject()) {
    DomainOssProject(
        name,
        description,
        url,
        topics,
        stars,
        private,
        fork,
        archived,
    )
}
