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

package com.cmgapps.android.curriculumvitae.usecase

import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.repository.EmploymentRepository
import kotlinx.coroutines.flow.Flow

class GetEmploymentsUseCase(private val repo: EmploymentRepository) {
    operator fun invoke(): Flow<List<Employment>> = repo.employments
}
