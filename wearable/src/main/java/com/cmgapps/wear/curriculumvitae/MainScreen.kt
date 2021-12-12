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

package com.cmgapps.wear.curriculumvitae

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.LocalContentAlpha
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import com.cmgapps.wear.curriculumvitae.ui.employment.EmploymentScreen
import com.cmgapps.wear.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.wear.curriculumvitae.ui.skills.SkillsScreen
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState

@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainScreen() {
    val pagerState = rememberPagerState()
    CompositionLocalProvider(LocalContentColor provides MaterialTheme.colors.onSurface) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colors.surface)
        ) {
            HorizontalPager(state = pagerState, count = 3) { page ->
                when (page) {
                    0 -> ProfileScreen()
                    1 -> EmploymentScreen()
                    2 -> SkillsScreen()
                }
            }

            HorizontalPagerIndicator(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 8.dp),
                pagerState = pagerState,
                activeColor = MaterialTheme.colors.primary.copy(LocalContentAlpha.current)
            )
        }
    }
}
