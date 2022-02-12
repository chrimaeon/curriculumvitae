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

import androidx.lifecycle.SavedStateHandle
import com.cmgapps.android.curriculumvitae.infra.NavArguments
import com.cmgapps.android.curriculumvitae.test.MainDispatcherExtension
import com.cmgapps.android.curriculumvitae.test.StubDomainEmployment
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetailViewModel
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(value = [MockitoExtension::class, MainDispatcherExtension::class])
class EmploymentDetailViewModelShould {

    @Mock
    lateinit var savedStateHandleMock: SavedStateHandle

    @Mock
    lateinit var store: Store<Int, Employment>

    private lateinit var viewModel: EmploymentDetailViewModel

    @BeforeEach
    fun beforeEach() {
        whenever(savedStateHandleMock.get<Int>(NavArguments.EMPLOYMENT_ID.argumentName)) doReturn 0
        whenever(store.stream(any())) doReturn flowOf(
            StoreResponse.Data(
                StubDomainEmployment(),
                ResponseOrigin.Fetcher
            )
        )
        viewModel = EmploymentDetailViewModel(savedStateHandleMock, store)
    }

    @Test
    fun `get employment`() = runTest {
        assertThat(viewModel.uiState.data, `is`(StubDomainEmployment()))
    }
}
