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

package com.cmgapps.wear.curriculumvitae.ui.skills

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import kotlinx.coroutines.launch
import java.io.IOException

class SkillsViewModel(
    repository: SkillsRepository
) : ViewModel() {

    var uiState: UiState<List<Skill>> by mutableStateOf(UiState(loading = true))
        private set

    init {
        viewModelScope.launch {
            uiState = try {
                val skills = repository.getSkills()
                UiState(data = skills.sortedByDescending { it.level })
            } catch (exc: IOException) {
                uiState.copy(networkError = true, exception = exc)
            }
        }
    }
}
