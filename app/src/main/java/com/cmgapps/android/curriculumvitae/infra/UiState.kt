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

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class UiState<out T : Any> {
    object Init : UiState<Nothing>()
    object Loading : UiState<Nothing>()
    data class Success<out T : Any>(val data: T) : UiState<T>()
    data class Error(val error: Throwable) : UiState<Nothing>()
}

private const val DELAY = 500L

private val uiStateDebounce: (UiState<Any>) -> Long = {
    when (it) {
        UiState.Loading -> DELAY
        else -> 0L
    }
}

// compile error java.lang.IllegalStateException: Type variable TypeVariable(T) should not be fixed!
// @OptIn(ExperimentalTime::class, FlowPreview::class)
// private fun <T> Flow<T>.debounceIf(
//     duration: Duration = 500L.milliseconds,
//     predicate: (T) -> Boolean
// ): Flow<T> = this.debounce {
//     when {
//         predicate(it) -> duration
//         else -> Duration.ZERO
//     }
// }

@OptIn(FlowPreview::class)
fun <T : Any, R : Any> (suspend () -> T).asUiStateFlow(
    @Suppress("UNCHECKED_CAST") mapping: T.() -> R = { this as R }
): Flow<UiState<R>> =
    flow {
        emit(UiState.Loading)
        try {
            emit(UiState.Success(invoke().mapping()))
        } catch (exc: Exception) {
            emit(UiState.Error(exc))
        }
    }.debounce(uiStateDebounce)

@OptIn(FlowPreview::class)
fun <T : Any, R : Any> Flow<T?>.asUiStateFlow(
    @Suppress("UNCHECKED_CAST") mapping: T.() -> R = { this as R }
): Flow<UiState<R>> =
    this.onStart { emit(null) }
        .map {
            if (it == null) {
                UiState.Loading
            } else {
                UiState.Success(it.mapping())
            }
        }
        .debounce(uiStateDebounce)
        .catch {
            emit(UiState.Error(it))
        }
