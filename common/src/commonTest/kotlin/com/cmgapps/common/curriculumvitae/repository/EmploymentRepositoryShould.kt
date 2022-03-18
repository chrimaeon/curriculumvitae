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

import StubDatabaseEmployment
import StubDomainEmployment
import app.cash.turbine.test
import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.IgnoreIos
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.cmgapps.common.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.runTest
import com.cmgapps.common.curriculumvitae.utils.MockCursor
import com.cmgapps.common.curriculumvitae.utils.MockSqlDriver
import com.cmgapps.common.curriculumvitae.utils.mockClient
import io.ktor.http.Url
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

@ExperimentalTime
class EmploymentRepositoryShould {

    private lateinit var repository: EmploymentRepository
    private lateinit var databaseWrapper: DatabaseWrapper

    @BeforeTest
    fun setup() {
        databaseWrapper =
            DatabaseWrapper { MockSqlDriver(MockCursor()) }
        repository = EmploymentRepository(
            CvApiService(mockClient, Url(BaseUrl)),
            databaseWrapper,
            Logger.withTag("Test"),
            CoroutineScope(Dispatchers.Default)
        )
    }

    /**
     * Fails on native;
     * [DatabaseWrapper.database][DatabaseWrapper.database] is not immutable
     */
    @IgnoreIos
    @Test
    fun get_employment_updates() = runTest {
        repository.getEmployments().test {
            // employments from api
            assertEquals(listOf(StubDomainEmployment()), awaitItem())

            val databaseEmployment = StubDatabaseEmployment().copy(id = 20)
            databaseWrapper {
                it.employmentQueries.insertEmployment(databaseEmployment)
            }

            // employments from api and database wrapper
            assertEquals(
                listOf(StubDomainEmployment(), databaseEmployment.asDomainModel()),
                awaitItem()
            )

            cancel()
        }
    }
}
