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

import StubNetworkEmployment
import StubNetworkProfile
import StubNetworkSkill
import StubNetworkStatus
import app.cash.turbine.test
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.runTest
import com.cmgapps.common.curriculumvitae.utils.mockClient
import io.ktor.http.Url
import kotlin.test.BeforeTest
import kotlin.test.Ignore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.ExperimentalTime

class CvApiServiceShould {

    private lateinit var apiService: CvApiService

    @BeforeTest
    fun setup() {
        apiService = CvApiService(mockClient, Url(BaseUrl))
    }

    @Test
    fun get_profile() = runTest {
        val result = apiService.getProfile()
        assertEquals(StubNetworkProfile(), result)
    }

    @Test
    fun get_employments() = runTest {
        val result = apiService.getEmployments()
        assertEquals(listOf(StubNetworkEmployment()), result)
    }

    @Test
    fun get_skills() = runTest {
        val result = apiService.getSkills()
        assertEquals(listOf(StubNetworkSkill()), result)
    }

    @ExperimentalTime
    @Test
    @Ignore // https://youtrack.jetbrains.com/issue/KTOR-537
    fun get_status() = runTest {
        apiService.getApiStatus().test {
            assertEquals(StubNetworkStatus(), awaitItem())
            awaitComplete()
        }
    }
}
