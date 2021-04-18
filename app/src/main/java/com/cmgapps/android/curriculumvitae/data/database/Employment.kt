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

package com.cmgapps.android.curriculumvitae.data.database

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.util.Objects
import com.cmgapps.android.curriculumvitae.data.domain.Employment as DomainEmployment
import com.cmgapps.shared.curriculumvitae.data.network.Employment as NetworkEmployment

@Entity
data class Employment(
    val jobTitle: String,
    val employer: String,
    val startDate: LocalDate,
    @PrimaryKey
    val id: Int,
    val endDate: LocalDate?,
    val city: String,
)

@Entity
data class Description(
    @PrimaryKey
    val id: Int,
    val employmentId: Int,
    val description: String,
)

data class EmploymentWithDescription(
    @Embedded val employment: Employment,
    @Relation(
        parentColumn = "id",
        entityColumn = "employmentId"
    )
    val description: List<Description>
)

fun List<EmploymentWithDescription>.asDomainModel() = map {
    DomainEmployment(
        it.employment.id,
        it.employment.jobTitle,
        it.employment.employer,
        it.employment.startDate,
        it.employment.endDate,
        it.employment.city,
        it.description.map { it.description }
    )
}

fun List<NetworkEmployment>.asDatabaseModel() = map {
    val employmentId = Objects.hash(it.employer, it.jobTitle, it.startDate.toEpochDay())
    EmploymentWithDescription(
        Employment(
            id = employmentId,
            jobTitle = it.jobTitle,
            employer = it.employer,
            startDate = it.startDate,
            endDate = it.endDate,
            city = it.city,
        ),
        it.description.map { desc ->
            Description(
                id = desc.hashCode(),
                employmentId = employmentId,
                description = desc,
            )
        }
    )
}
