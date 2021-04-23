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

sealed class Resource<out T : Any> {
    object Loading : Resource<Nothing>()
    data class Success<out T : Any>(val data: T) : Resource<T>()
    data class Error(val error: Throwable) : Resource<Nothing>()
}

fun <T : Any, R : Any> (suspend () -> T).asLoadingResourceFlow(mapping: T.() -> R): Flow<Resource<R>> =
    flow {
        emit(Resource.Loading)
        try {
            emit(Resource.Success(invoke().mapping()))
        } catch (exc: Exception) {
            emit(Resource.Error(exc))
        }
    }

fun <T : Any, R : Any> Flow<T?>.asLoadingResourceFlow(mapping: T.() -> R): Flow<Resource<R>> =
    this.map {
        if (it == null) {
            Resource.Loading
        } else {
            Resource.Success(it.mapping())
        }
    }.catch {
        emit(Resource.Error(it))
    }
