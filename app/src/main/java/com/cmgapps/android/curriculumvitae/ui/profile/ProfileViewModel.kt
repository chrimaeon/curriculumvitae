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
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@LogTag
@HiltViewModel
class ProfileViewModel @Inject constructor(
    profileStore: Store<String, Profile>
) : ViewModel() {

    var uiState: UiState<Profile> by mutableStateOf(UiState(loading = true))
        private set

    init {
        viewModelScope.launch {
            profileStore.stream(StoreRequest.cached("profile", refresh = true))
                .collect { response ->
                    when (response) {
                        is StoreResponse.Loading -> uiState = uiState.copy(loading = true)
                        is StoreResponse.Data ->
                            uiState = UiState(data = response.value)
                        is StoreResponse.Error -> {
                            val origin = response.origin
                            val exception = Exception(
                                response.errorMessageOrNull(),
                                if (response is StoreResponse.Error.Exception) response.error else null
                            )
                            uiState = uiState.copy(
                                loading = origin == ResponseOrigin.SourceOfTruth,
                                networkError = origin == ResponseOrigin.Fetcher,
                                exception = exception
                            )
                            Timber.tag(LOG_TAG).e(exception)
                        }
                        is StoreResponse.NoNewData -> {
                            if (BuildConfig.DEBUG) {
                                Timber.tag(LOG_TAG).d("No new data")
                            }
                        }
                    }
                }
        }
    }
}
