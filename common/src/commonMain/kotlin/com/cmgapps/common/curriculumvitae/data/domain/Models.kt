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

import com.cmgapps.common.curriculumvitae.resource.R
import com.cmgapps.common.curriculumvitae.resource.plurals
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.periodUntil
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment
import com.cmgapps.common.curriculumvitae.data.network.Profile as NetworkProfile
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
    val profileImageUrl: String,
    val address: Address,
    val email: String,
    val intro: List<String>,
)

data class Address(val street: String, val city: String, val postalCode: String)

fun NetworkProfile.asDomainModel() = Profile(
    this.name,
    this.phone,
    this.profileImageUrl,
    Address(this.address.street, this.address.city, this.address.postalCode),
    this.email,
    this.intro
)

data class Employment(
    val id: Int,
    val jobTitle: String,
    val employer: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val city: String,
    val description: List<String>
) {
    val workPeriod: DatePeriod
        get() = startDate.periodUntil(
            endDate ?: Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
        ).plus(DatePeriod(months = 1))
}

fun DatePeriod.asHumanReadableString() = buildString {
    if (years > 0) {
        append(plurals(R.plurals.years, years))
    }
    append(' ')
    if (months > 0) {
        append(plurals(R.plurals.months, months))
    }
}.trim()

fun List<NetworkEmployment>.asDomainModel() = map {
    Employment(
        it.hashCode(),
        it.jobTitle,
        it.employer,
        it.startDate,
        it.endDate,
        it.city,
        it.description
    )
}
