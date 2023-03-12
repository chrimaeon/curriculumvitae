/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.ui.ossprojects

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.dropbox.android.external.store4.ResponseOrigin
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import com.dropbox.android.external.store4.fresh
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@LogTag
@HiltViewModel
class OssProjectsViewModel @Inject constructor(
    val store: Store<String, List<OssProject>>,
) : ViewModel() {

    var uiState: UiState<List<OssProject>> by mutableStateOf(UiState(loading = true))
        private set

    init {
        viewModelScope.launch {
            store.stream(StoreRequest.cached(KEY, refresh = true))
                .collect { response ->
                    when (response) {
                        is StoreResponse.Loading -> uiState = uiState.copy(loading = true)
                        is StoreResponse.Data ->
                            uiState = UiState(data = response.value)
                        is StoreResponse.Error -> {
                            val origin = response.origin
                            val exception = Exception(
                                response.errorMessageOrNull(),
                                if (response is StoreResponse.Error.Exception) response.error else null,
                            )
                            uiState = uiState.copy(
                                loading = origin == ResponseOrigin.SourceOfTruth,
                                networkError = origin == ResponseOrigin.Fetcher,
                                exception = exception,
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

    fun refresh() {
        viewModelScope.launch {
            uiState = uiState.copy(loading = true)
            uiState = try {
                UiState(data = store.fresh(KEY))
            } catch (exc: Exception) {
                Timber.tag(LOG_TAG).e(exc)
                uiState.copy(networkError = true, exception = exc)
            }
        }
    }

    companion object {
        private const val KEY = "ossProjects"
    }
}
