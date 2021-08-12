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

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation
import kotlinx.datetime.LocalDate
import com.cmgapps.common.curriculumvitae.data.domain.Employment as DomainEmployment
import com.cmgapps.common.curriculumvitae.data.network.Employment as NetworkEmployment

@Entity(
    tableName = "employment"
)
data class Employment(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "job_title")
    val jobTitle: String,
    val employer: String,
    @ColumnInfo(name = "start_date")
    val startDate: LocalDate,
    @ColumnInfo(name = "end_date")
    val endDate: LocalDate?,
    val city: String,
)

@Entity(
    tableName = "description",
    foreignKeys = [
        ForeignKey(
            entity = Employment::class,
            parentColumns = ["id"],
            childColumns = ["employment_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [Index("employment_id")]
)
data class Description(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "employment_id")
    val employmentId: Int,
    val description: String,
)

data class EmploymentWithDescription(
    @Embedded val employment: Employment,
    @Relation(
        parentColumn = "id",
        entityColumn = "employment_id"
    )
    val description: List<Description>
)

fun List<EmploymentWithDescription>.asDomainModel() = map { it.asDomainModel() }

fun EmploymentWithDescription.asDomainModel() = DomainEmployment(
    employment.id,
    employment.jobTitle,
    employment.employer,
    employment.startDate,
    employment.endDate,
    employment.city,
    description.map { it.description }
)

fun List<NetworkEmployment>.asDatabaseModel() = map {
    val employmentId = it.hashCode()
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
