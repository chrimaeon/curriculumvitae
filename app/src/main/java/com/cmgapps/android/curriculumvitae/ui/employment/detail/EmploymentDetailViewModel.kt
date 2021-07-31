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

package com.cmgapps.android.curriculumvitae.ui.employment.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.NavArguments
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.usecase.GetEmploymentWithIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmploymentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    getEmploymentUseCase: GetEmploymentWithIdUseCase
) : ViewModel() {

    var uiState: UiState<Employment> by mutableStateOf(UiState(loading = true))

    init {
        viewModelScope.launch {
            getEmploymentUseCase(
                savedStateHandle[NavArguments.EMPLOYMENT_ID.argumentName] ?: error(
                    "EmploymentId not set"
                )
            )
                .catch { uiState = uiState.copy(loading = false, exception = it) }
                .collect { uiState = UiState(data = it) }
        }
    }
}
