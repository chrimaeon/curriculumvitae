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

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.usecase.GetProfileUseCase
import com.cmgapps.android.curriculumvitae.usecase.RefreshProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

@LogTag
@HiltViewModel
class ProfileViewModel @Inject constructor(
    getProfile: GetProfileUseCase,
    refreshProfileUseCase: RefreshProfileUseCase
) : ViewModel() {

    var uiState: UiState<Profile> by mutableStateOf(UiState(loading = true))
        private set

    init {
        viewModelScope.launch {
            uiState = try {
                refreshProfileUseCase()
                uiState.copy(loading = false)
            } catch (exc: IOException) {
                Timber.tag(LOG_TAG).e(exc)
                uiState.copy(loading = false, networkError = true, exception = exc)
            }
        }

        viewModelScope.launch {
            getProfile().catch { error ->
                Timber.tag(LOG_TAG).e(error)
                uiState.copy(exception = error, networkError = false)
            }.collect { if (it != null) uiState = UiState(data = it) }
        }
    }
}
