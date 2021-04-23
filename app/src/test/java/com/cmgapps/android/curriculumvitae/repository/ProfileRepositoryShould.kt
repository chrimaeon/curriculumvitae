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

import androidx.datastore.core.DataStore
import com.cmgapps.android.curriculumvitae.data.datastore.Profile
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.test.MainDispatcherExtension
import com.cmgapps.android.curriculumvitae.test.StubDataStoreProfile
import com.cmgapps.android.curriculumvitae.test.StubDomainProfile
import com.cmgapps.android.curriculumvitae.test.StubNetworkProfile
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
import org.mockito.Mockito.any
import org.mockito.Mockito.verify
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(value = [MockitoExtension::class, MainDispatcherExtension::class])
internal class ProfileRepositoryShould {

    @Mock
    lateinit var apiService: CvApiService

    @Mock
    lateinit var dataStore: DataStore<Profile?>

    private lateinit var repository: ProfileRepository

    @BeforeEach
    fun before() {
        `when`(dataStore.data).thenReturn(flowOf(StubDataStoreProfile()))
        repository = ProfileRepository(apiService, dataStore, TestCoroutineDispatcher())
    }

    @Nested
    @DisplayName("profile flow")
    internal inner class ProfileFlow {

        @Test
        fun `emit success`() = runBlockingTest {

            val result = repository.profile.single()
            assertThat(result, instanceOf(Resource.Success::class.java))
        }

        @Test
        fun `emit employments`() = runBlockingTest {

            val result = repository.profile.single()
            assertThat(
                (result as Resource.Success).data,
                `is`(StubDomainProfile())
            )
        }
    }

    @Nested
    @DisplayName("refresh Profile")
    @ExtendWith(MainDispatcherExtension::class)
    internal inner class RefreshProfile {

        @Test
        fun `call getProfile`() = runBlockingTest {
            `when`(apiService.getProfile()).thenReturn(StubNetworkProfile())

            repository.refreshProfile()
            verify(apiService).getProfile()
        }

        fun `update data store`() = runBlockingTest {
            `when`(apiService.getProfile()).thenReturn(StubNetworkProfile())

            repository.refreshProfile()
            verify(dataStore).updateData(any())
        }
    }
}
