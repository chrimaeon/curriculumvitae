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

package com.cmgapps.android.curriculumvitae.repository

import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.data.database.EmploymentDao
import com.cmgapps.android.curriculumvitae.data.database.asDatabaseModel
import com.cmgapps.android.curriculumvitae.data.database.asDomainModel
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.infra.asLoadingResourceFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import kotlin.coroutines.CoroutineContext

@LogTag
class EmploymentRepository(
    private val api: CvApiService,
    private val employmentDao: EmploymentDao,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    val employment: Flow<Resource<List<Employment>>> =
        employmentDao.getEmployments().asLoadingResourceFlow { asDomainModel() }

    suspend fun refreshEmployments() {
        withContext(coroutineContext) {
            try {
                delay(3000)
                employmentDao.insertAll(api.getEmployment().asDatabaseModel())
            } catch (exc: IOException) {
                Timber.tag(LOG_TAG).e(exc)
            }
        }
    }
}
