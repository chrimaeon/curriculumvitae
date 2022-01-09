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

package com.cmgapps.common.curriculumvitae.repository

import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import io.ktor.utils.io.errors.IOException
import kotlin.coroutines.cancellation.CancellationException

class ProfileRepository(private val api: CvApiService) {
    @Throws(IOException::class, CancellationException::class)
    suspend fun getProfile(): Profile = api.getProfile().asDomainModel()

    suspend fun getProfileImage(imagePath: String) = api.getAsset(imagePath)
}
