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

package com.cmgapps.wear.curriculumvitae.ui.employment

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.IOException

class EmploymentViewModel(
    employmentRepository: EmploymentRepository
) : ViewModel() {

    var uiState: UiState<List<Employment>> by mutableStateOf(UiState(loading = true))
        private set

    init {
        viewModelScope.launch {
            try {
                employmentRepository.getEmployments().collect {
                    uiState = UiState(data = it)
                }
            } catch (exc: IOException) {
                uiState = uiState.copy(networkError = true, exception = exc)
            }
        }
    }
}
