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

package com.cmgapps.web.curriculumvitae.repository

import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.web.curriculumvitae.data.domain.asDomainModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import com.cmgapps.common.curriculumvitae.data.network.Status as NetworkModel
import com.cmgapps.web.curriculumvitae.data.domain.Status as DomainStatus

class StatusRepository(private val api: CvApiService) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getStatus(): Flow<DomainStatus> = api.getApiStatus()
        .map(NetworkModel::asDomainModel)
}
