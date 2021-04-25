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

package com.cmgapps.android.curriculumvitae.infra

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

sealed class UiState<out T : Any> {
    object Loading : UiState<Nothing>()
    data class Success<out T : Any>(val data: T) : UiState<T>()
    data class Error(val error: Throwable) : UiState<Nothing>()
}

fun <T : Any, R : Any> (suspend () -> T).asUiStateFlow(mapping: T.() -> R): Flow<UiState<R>> =
    flow {
        emit(UiState.Loading)
        try {
            emit(UiState.Success(invoke().mapping()))
        } catch (exc: Exception) {
            emit(UiState.Error(exc))
        }
    }

fun <T : Any, R : Any> Flow<T?>.asUiStateFlow(
    @Suppress("UNCHECKED_CAST") mapping: T.() -> R = { this as R }
): Flow<UiState<R>> =
    this.map {
        if (it == null) {
            UiState.Loading
        } else {
            UiState.Success(it.mapping())
        }
    }.catch {
        emit(UiState.Error(it))
    }
