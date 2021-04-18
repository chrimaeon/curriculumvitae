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

import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.infra.Resource
import com.cmgapps.android.curriculumvitae.infra.asLoadingResourceFlow
import kotlinx.coroutines.flow.Flow

class ProfileRepository(private val api: CvApiService) {
    val profile: Flow<Resource<Profile>> = api::getProfile.asLoadingResourceFlow { asDomainModel() }
}
