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

package com.cmgapps.android.curriculumvitae.ui.employment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.usecase.GetEmploymentsUseCase
import com.cmgapps.android.curriculumvitae.usecase.RefreshEmploymentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@LogTag
@HiltViewModel
class EmploymentViewModel @Inject constructor(
    getEmploymentsUseCase: GetEmploymentsUseCase,
    private val refreshEmploymentUseCase: RefreshEmploymentUseCase
) : ViewModel() {

    var uiState: UiState<List<Employment>?> by mutableStateOf(UiState(loading = true))
        private set

    fun refresh() {
        viewModelScope.launch {
            try {
                uiState = uiState.copy(loading = true)
                refreshEmploymentUseCase()
                uiState = uiState.copy(loading = false)
            } catch (exc: IOException) {
                Timber.tag(LOG_TAG).e(exc)
                uiState = uiState.copy(loading = false, exception = exc, networkError = true)
            }
        }
    }

    init {
        viewModelScope.launch {
            getEmploymentsUseCase().collect { uiState = UiState(data = it) }
        }

        refresh()
    }
}
