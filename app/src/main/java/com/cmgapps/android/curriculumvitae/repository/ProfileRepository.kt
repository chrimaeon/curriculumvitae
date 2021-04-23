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

import androidx.datastore.core.DataStore
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.data.datastore.asDataStoreModel
import com.cmgapps.android.curriculumvitae.data.datastore.asDomainModel
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.infra.asLoadingResourceFlow
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext
import com.cmgapps.android.curriculumvitae.data.datastore.Profile as DataStoreProfile
import com.cmgapps.android.curriculumvitae.data.domain.Profile as DomainProfile

@LogTag
class ProfileRepository(
    private val api: CvApiService,
    private val profileDataStore: DataStore<DataStoreProfile?>,
    private val coroutineContext: CoroutineContext = Dispatchers.IO
) {
    val profile: Flow<Resource<DomainProfile>> =
        profileDataStore.data.asLoadingResourceFlow { asDomainModel() }

    suspend fun refreshProfile() {
        withContext(coroutineContext) {
            try {
                val profile = api.getProfile()
                profileDataStore.updateData { profile.asDataStoreModel() }
            } catch (exc: Exception) {
                Timber.tag(LOG_TAG).e(exc)
            }
        }
    }
}
