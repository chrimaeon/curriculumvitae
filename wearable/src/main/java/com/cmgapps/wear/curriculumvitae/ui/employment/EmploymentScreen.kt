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

package com.cmgapps.wear.curriculumvitae.ui.employment

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.Text
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import org.koin.androidx.compose.getViewModel

private val itemHeight = 80.dp

@Composable
fun EmploymentScreen(viewModel: EmploymentViewModel = getViewModel()) {

    val uiState = viewModel.uiState

    BoxWithConstraints {
        val verticalPadding = (maxHeight / 2) - (itemHeight / 2)

        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = verticalPadding, horizontal = 16.dp),
            scalingParams = ScalingLazyColumnDefaults.scalingParams(
                edgeScale = 0.2f,
                minTransitionArea = 0.5f
            )
        ) {

            val isLoading = uiState.loading
            val itemCount = uiState.data?.size ?: 5

            items(itemCount) { index ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(itemHeight)
                        .clip(RoundedCornerShape(percent = 50))
                        .background(MaterialTheme.colors.secondary)
                        .placeholder(
                            visible = isLoading,
                            highlight = PlaceholderHighlight.shimmer(),
                        )
                        .padding(start = itemHeight / 4, end = itemHeight / 4),
                    verticalArrangement = Arrangement.Center
                ) {
                    val employments = uiState.data
                    if (employments != null) {
                        val employment = employments[index]
                        Text(
                            employment.employer,
                            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            employment.workPeriod.asHumanReadableString(),
                            style = MaterialTheme.typography.body2.copy(fontSize = 12.sp),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            employment.jobTitle,
                            style = MaterialTheme.typography.body1,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
            }
        }
    }
}
