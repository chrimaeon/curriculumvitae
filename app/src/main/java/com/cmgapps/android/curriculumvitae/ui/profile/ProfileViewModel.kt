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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.infra.asUiStateFlow
import com.cmgapps.android.curriculumvitae.usecase.GetProfileUseCase
import com.cmgapps.android.curriculumvitae.usecase.RefreshProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    getProfile: GetProfileUseCase,
    refreshProfileUseCase: RefreshProfileUseCase
) :
    ViewModel() {

    val profile: Flow<UiState<Profile>> = getProfile().asUiStateFlow()

    init {
        viewModelScope.launch {
            refreshProfileUseCase()
        }
    }
}
