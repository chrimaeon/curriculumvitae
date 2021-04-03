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

package com.cmgapps.android.curriculumvitae.ui.profile

import androidx.lifecycle.MutableLiveData
import com.cmgapp.curriculumvitae.data.Profile
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.test.InstantTaskExecutorExtension
import com.cmgapps.android.curriculumvitae.test.StubProfile
import com.cmgapps.android.curriculumvitae.test.getOrAwaitValue
import com.cmgapps.android.curriculumvitae.usecase.GetProfileUseCase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(value = [MockitoExtension::class, InstantTaskExecutorExtension::class])
internal class ProfileViewModelShould {

    @Mock
    lateinit var getProfileUseCase: GetProfileUseCase
    private lateinit var viewModel: ProfileViewModel
    private lateinit var profile: Profile

    @BeforeEach
    fun beforeEach() {
        profile = StubProfile()
        `when`(getProfileUseCase.invoke()).thenReturn(MutableLiveData(Resource.Success(profile)))
        viewModel = ProfileViewModel(getProfileUseCase)
    }

    @Test
    fun `get profile`() {
        assertThat((viewModel.profile.getOrAwaitValue() as Resource.Success).data, `is`(profile))
    }
}
