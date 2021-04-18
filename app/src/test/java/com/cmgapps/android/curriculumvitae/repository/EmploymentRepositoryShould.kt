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

import com.cmgapps.android.curriculumvitae.data.database.EmploymentDao
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.test.MainDispatcherExtension
import com.cmgapps.android.curriculumvitae.test.StubDomainEmployment
import com.cmgapps.android.curriculumvitae.test.StubEmploymentWithDescription
import com.cmgapps.android.curriculumvitae.test.StubNetworkEmployment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(value = [MockitoExtension::class, MainDispatcherExtension::class])
internal class EmploymentRepositoryShould {

    @Mock
    lateinit var apiService: CvApiService

    @Mock
    lateinit var employmentDao: EmploymentDao

    private lateinit var repository: EmploymentRepository

    @BeforeEach
    fun beforeEach() {
        `when`(employmentDao.getEmployments()).thenReturn(
            flowOf(
                listOf(
                    StubEmploymentWithDescription()
                )
            )
        )
        repository = EmploymentRepository(apiService, employmentDao, TestCoroutineDispatcher())
    }

    @Nested
    @DisplayName("employment flow")
    internal inner class EmploymentFlow {

        @Test
        fun `emit success`() = runBlockingTest {

            val result = repository.employment.single()
            assertThat(result, instanceOf(Resource.Success::class.java))
        }

        @Test
        fun `emit employments`() = runBlockingTest {

            val result = repository.employment.single()
            assertThat(
                (result as Resource.Success).data,
                `is`(listOf(StubDomainEmployment()))
            )
        }
    }

    @Nested
    @DisplayName("refreshEmployments")
    @ExtendWith(MainDispatcherExtension::class)
    internal inner class RefreshEmployments {

        @Test
        fun `call getEmployments`() = runBlockingTest {
            `when`(apiService.getEmployment()).thenReturn(listOf(StubNetworkEmployment()))

            repository.refreshEmployments()
            verify(apiService).getEmployment()
        }

        @Test
        fun `insert employments`() = runBlockingTest {
            `when`(apiService.getEmployment()).thenReturn(listOf(StubNetworkEmployment()))

            repository.refreshEmployments()
            verify(employmentDao).insertAll(listOf(StubEmploymentWithDescription()))
        }
    }
}
