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

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.cmgapps.desktop.curriculumvitae.components.EmploymentCard
import com.cmgapps.desktop.curriculumvitae.components.ProfileCard
import com.cmgapps.desktop.curriculumvitae.components.SkillsCard
import com.cmgapps.desktop.curriculumvitae.ui.Footer
import org.koin.core.Koin
import java.io.IOException

@OptIn(ExperimentalFoundationApi::class, ExperimentalStdlibApi::class)
@Composable
fun App(koin: Koin) {

    val employmentRepo: EmploymentRepository = koin.get()
    val skillsRepo: SkillsRepository = koin.get()

    var skills: List<Skill>? by remember { mutableStateOf(null) }

    LaunchedEffect(skillsRepo) {
        skills = try {
            skillsRepo.getSkills()
        } catch (exc: IOException) {
            null
        }
    }

    val employments by employmentRepo.getEmployments().collectAsState(emptyList())

    Scaffold(
        modifier = Modifier.background(MaterialTheme.colors.surface),
        bottomBar = {
            Footer()
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {

            Box {
                val state = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    state = state,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(20.dp)
                ) {
                    items(
                        count = employments.size + 2,
                    ) { index ->
                        when {
                            index == 0 -> ProfileCard(profileRepository = koin.get())
                            (employments.isEmpty() && index == 1) ||
                                (employments.isNotEmpty() && index == employments.size + 1) -> SkillsCard(skills)
                            else -> EmploymentCard(employments[index - 1])
                        }
                    }
                }
                VerticalScrollbar(
                    modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                    adapter = rememberScrollbarAdapter(
                        scrollState = state
                    )
                )
            }
        }
    }
}
