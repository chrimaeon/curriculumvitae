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

package com.cmgapps.common.curriculumvitae.data.network

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import com.cmgapps.common.curriculumvitae.data.db.Employment as DbEmployment

@Serializable
data class Employment(
    val id: Int,
    val jobTitle: String,
    val employer: String,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val city: String,
    val description: List<String>,
)

fun List<Employment>.asDatabaseModel() = map { it.asDatabaseModel() }

fun Employment.asDatabaseModel() = DbEmployment(
    this.id,
    this.jobTitle,
    this.employer,
    this.startDate.toString(),
    this.endDate?.toString(),
    this.city,
    this.description,
)
