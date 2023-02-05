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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.items
import androidx.wear.compose.material.rememberScalingLazyListState
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.wear.curriculumvitae.ui.Theme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import org.koin.androidx.compose.getViewModel

@Composable
fun SkillsScreen(
    viewModel: SkillsViewModel = getViewModel(),
) {
    val state = viewModel.uiState

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    shape = CircleShape,
                ),
        )
    }

    state.data?.let { Content(it) }
}

private val itemHeight = 40.dp

@Composable
private fun Content(skills: List<Skill>) {
    val initialOffset = with(LocalDensity.current) {
        itemHeight.roundToPx()
    }
    val listState = rememberScalingLazyListState(
        initialCenterItemIndex = 0,
        initialCenterItemScrollOffset = -initialOffset,
    )
    ScalingLazyColumn(
        state = listState,
        scalingParams = ScalingLazyColumnDefaults.scalingParams(
            edgeScale = 0.3f,
            minTransitionArea = 0.5f,
        ),
        autoCentering = AutoCenteringParams(itemIndex = 0),
    ) {
        items(skills) {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.secondary, RoundedCornerShape(percent = 50))
                    .padding(4.dp)
                    .fillMaxWidth()
                    .height(itemHeight),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    it.name,
                    style = MaterialTheme.typography.title2,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Preview(
    widthDp = 195,
    heightDp = 195,
)
@Composable
fun SkillsScreenPreview() {
    Theme {
        Content(
            listOf(
                Skill("Skill 1", 1),
                Skill("Skill 2", 2),
            ),
        )
    }
}
