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
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.NavArguments
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.get
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@LogTag("EmploymentDetailVM")
@HiltViewModel
class EmploymentDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    store: Store<Int, Employment>
) : ViewModel() {

    var uiState: UiState<Employment> by mutableStateOf(UiState(loading = true))

    init {
        viewModelScope.launch {
            uiState = try {
                val id: Int = savedStateHandle[NavArguments.EMPLOYMENT_ID.argumentName]
                    ?: throw IllegalArgumentException("ID not set on navigation")
                UiState(data = store.get(id))
            } catch (exc: Exception) {
                Timber.tag(LOG_TAG).e(exc)
                UiState(exception = exc)
            }
        }
    }
}
