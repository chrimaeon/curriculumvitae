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

import com.cmgapp.shared.curriculumvitae.data.network.Profile
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.test.InstantTaskExecutorExtension
import com.cmgapps.android.curriculumvitae.test.MainDispatcherExtension
import com.cmgapps.android.curriculumvitae.test.StubProfile
import com.cmgapps.android.curriculumvitae.test.getOrAwaitValue
import com.cmgapps.android.curriculumvitae.test.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.instanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(value = [MockitoExtension::class, InstantTaskExecutorExtension::class, MainDispatcherExtension::class])
internal class ProfileRepositoryShould {

    @Mock
    lateinit var apiService: CvApiService

    private lateinit var profile: Profile
    private lateinit var repository: ProfileRepository

    @BeforeEach
    fun before() {
        profile = StubProfile()

        repository = ProfileRepository(apiService)
    }

    @Test
    fun `emit loading`() = runBlockingTest {
        `when`(apiService.getProfile()).thenReturn(profile)

        val resource = repository.profile.getOrAwaitValue()

        assertThat(resource, instanceOf(Resource.Loading::class.java))
    }

    @Test
    fun `emit success resource`() = runBlockingTest {
        `when`(apiService.getProfile()).thenReturn(profile)

        repository.profile.observeForTesting {
            assertThat(repository.profile.value, instanceOf(Resource.Success::class.java))
        }
    }

    @Test
    fun `emit success resource with profile`() = runBlockingTest {
        `when`(apiService.getProfile()).thenReturn(profile)

        repository.profile.observeForTesting {
            assertThat((repository.profile.value as Resource.Success).data, `is`(profile))
        }
    }
}
