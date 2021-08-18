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

package com.cmgapps.common.curriculumvitae.repository

import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.employmentMapper
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class EmploymentRepository(
    private val api: CvApiService,
    private val databaseWrapper: DatabaseWrapper,
) : CoroutineScope by MainScope() {

    fun getEmployments(): Flow<List<Employment>> = flow {
        databaseWrapper { db ->
            db.employmentQueries.selectAll(::employmentMapper).asFlow().mapToList().collect {
                emit(it)
            }
        }
    }

    init {
        try {
            launch {
                val employments = api.getEmployments()
                databaseWrapper {
                    it.employmentQueries.let { employmentDao ->
                        employmentDao.transaction {
                            employments.forEach { employment ->
                                employmentDao.insertEmployment(
                                    id = employment.hashCode(),
                                    job_title = employment.jobTitle,
                                    employer = employment.employer,
                                    start_date = employment.startDate.toString(),
                                    end_date = employment.endDate?.toString(),
                                    city = employment.city,
                                    description = employment.description
                                )
                            }
                        }
                    }
                }
            }
        } catch (exc: Exception) {
            exc.printStackTrace()
        }
    }
}
