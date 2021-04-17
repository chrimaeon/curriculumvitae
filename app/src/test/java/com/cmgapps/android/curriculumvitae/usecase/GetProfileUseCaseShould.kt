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

package com.cmgapps.android.curriculumvitae.usecase

import androidx.lifecycle.MutableLiveData
import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.repository.ProfileRepository
import com.cmgapps.android.curriculumvitae.test.StubProfile
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class GetProfileUseCaseShould {

    @Mock
    lateinit var repository: ProfileRepository

    private lateinit var userCase: GetProfileUseCase
    private lateinit var profile: Profile

    @BeforeEach
    fun before() {
        profile = StubProfile().asDomainModel()
        userCase = GetProfileUseCase(repository)
    }

    @Test
    fun `return profile livedata`() {
        `when`(repository.profile).thenReturn(MutableLiveData(Resource.Success(profile)))

        val profileLivedata = userCase()

        assertThat((profileLivedata.value as Resource.Success).data, `is`(profile))
    }
}
