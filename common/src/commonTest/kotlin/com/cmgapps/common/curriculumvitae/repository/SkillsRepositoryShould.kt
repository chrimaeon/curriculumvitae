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

import StubDomainSkill
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.runTest
import com.cmgapps.common.curriculumvitae.utils.mockClient
import io.ktor.http.Url
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SkillsRepositoryShould {

    private lateinit var repository: SkillsRepository

    @BeforeTest
    fun setup() {
        repository = SkillsRepository(CvApiService(mockClient, Url(BaseUrl)))
    }

    @Test
    fun get_domain_skill() = runTest {
        val result = repository.getSkills()
        assertEquals(listOf(StubDomainSkill()), result)
    }
}
