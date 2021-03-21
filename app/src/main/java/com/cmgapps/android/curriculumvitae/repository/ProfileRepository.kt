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

package com.cmgapps.android.curriculumvitae.repository

import androidx.lifecycle.liveData
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import timber.log.Timber

class ProfileRepository(private val api: CvApiService) {
    fun getProfile() = liveData {
        emit(Resource.Loading)
        try {
            val profile = api.getProfile()
            Timber.tag("ProfileRepository").d(profile.toString())
            emit(Resource.Success(profile))
        } catch (exc: Exception) {
            emit(Resource.Error(exc))
        }
    }
}
