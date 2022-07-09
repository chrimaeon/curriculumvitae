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

package com.cmgapps.common.curriculumvitae.data.domain

import com.cmgapps.common.curriculumvitae.resource.Res
import com.cmgapps.common.curriculumvitae.resource.plurals
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import com.cmgapps.common.curriculumvitae.data.db.Employment as DatabaseEmployment
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile
import com.cmgapps.common.curriculumvitae.data.network.Skill as NetworkSkill
import com.cmgapps.common.curriculumvitae.data.network.Status as NetworkStatus

data class Status(
    val availableProcessors: Int,
    val freeMemory: Long,
    val totalMemory: Long,
    val maxMemory: Long,
)

fun NetworkStatus.asDomainModel() =
    Status(this.availableProcessors, this.freeMemory, this.totalMemory, this.maxMemory)

data class Profile(
    val name: String,
    val phone: String,
    val profileImagePath: String,
    val address: Address,
    val email: String,
    val intro: List<String>,
)

data class Address(val street: String, val city: String, val postalCode: String)

fun NetworkProfile.asDomainModel() = Profile(
    this.name,
    this.phone,
    this.profileImagePath,
    Address(this.address.street, this.address.city, this.address.postalCode),
    this.email,
    this.intro,
)

data class Employment(
    val id: Int,
    val jobTitle: String,
    val employer: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val city: String,
    val description: List<String>,
) {
    val workPeriod: DatePeriod
        get() = startDate.periodUntil(
            endDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        ).plus(DatePeriod(months = 1))
}

fun employmentMapper(
    id: Int,
    jobTitle: String,
    employer: String,
    startDate: String,
    endDate: String?,
    city: String,
    description: List<String>,
) = Employment(
    id,
    jobTitle,
    employer,
    LocalDate.parse(startDate),
    endDate?.let { LocalDate.parse(it) },
    city,
    description,
)

fun DatabaseEmployment.asDomainModel() =
    Employment(
        this.id,
        this.job_title,
        this.employer,
        LocalDate.parse(this.start_date),
        this.end_date?.let { LocalDate.parse(it) },
        this.city,
        description,
    )

fun DatePeriod.asHumanReadableString() = buildString {
    if (years > 0) {
        try {
            append(plurals(Res.plurals.years, years))
        } catch (exc: Throwable) {
            exc.printStackTrace()
        }
    }
    append(' ')
    if (months > 0) {
        append(plurals(Res.plurals.months, months))
    }
}.trim()

data class Skill(val name: String, val level: Int)

fun List<NetworkSkill>.asDomainModel() = map { Skill(it.name, it.level) }
