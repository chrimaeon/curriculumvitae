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

package com.cmgapps.android.curriculumvitae.ui.employment.details

import app.cash.turbine.test
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.test.StubDomainEmployment
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetailViewModel
import com.cmgapps.android.curriculumvitae.usecase.GetEmploymentWithIdUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
@ExtendWith(MockitoExtension::class)
class EmploymentDetailViewModelShould {

    @Mock
    lateinit var getEmploymentWithIdUseCase: GetEmploymentWithIdUseCase

    private lateinit var viewModel: EmploymentDetailViewModel

    @BeforeEach
    fun beforeEach() {
        `when`(getEmploymentWithIdUseCase.invoke(anyInt()))
            .thenReturn(flowOf(StubDomainEmployment()))
        viewModel = EmploymentDetailViewModel(getEmploymentWithIdUseCase)
    }

    @Test
    fun `get employment`() = runBlockingTest {
        viewModel.employment(0).test {
            assertThat((expectItem() as UiState.Success).data, `is`(StubDomainEmployment()))
            expectComplete()
        }
    }
}
