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

package com.cmgapps.android.curriculumvitae.ui.employment

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.AnimatedCard
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.ExperimentalTime

@LogTag
@Composable
fun EmploymentScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: EmploymentViewModel,
    snackbarHostState: SnackbarHostState,
    navigateToEmploymentDetails: (employmentId: Int) -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val uiState = viewModel.uiState

        if (uiState.networkError) {
            val errorMessage = stringResource(id = R.string.refresh_error)
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }

        if (uiState.exception != null && !uiState.networkError && uiState.data.isNullOrEmpty()) {
            ContentError()
        }

        Content(
            uiState = uiState,
            bottomContentPadding = bottomContentPadding,
            navigateToEmploymentDetails = navigateToEmploymentDetails,
            onRefresh = {
                viewModel.refresh()
            },
        )
    }
}

@Composable
private fun Content(
    uiState: UiState<List<Employment>?>,
    bottomContentPadding: Dp,
    navigateToEmploymentDetails: (employmentId: Int) -> Unit,
    onRefresh: () -> Unit = {},
) {
    SwipeRefresh(
        state = rememberSwipeRefreshState(uiState.loading),
        onRefresh = onRefresh,
        indicator = { state, trigger ->
            SwipeRefreshIndicator(
                state = state,
                refreshTriggerDistance = trigger,
                contentColor = MaterialTheme.colors.secondaryVariant,
                refreshingOffset = with(LocalDensity.current) { LocalWindowInsets.current.statusBars.top.toDp() + 16.dp }
            )
        }
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyBottom = false,
                additionalStart = 2.dp,
                additionalTop = 8.dp,
                additionalEnd = 16.dp,
                additionalBottom = bottomContentPadding
            )
        ) {
            val employments = uiState.data
            if (employments != null) {
                if (employments.isNotEmpty()) {
                    itemsIndexed(
                        employments,
                        key = { _, employment -> employment.id },
                    ) { index, employment ->
                        val isFirst = index == 0
                        val isLast = index == employments.size - 1
                        EmploymentCard(
                            employment = employment,
                            isFirst = isFirst,
                            isLast = isLast,
                            navigateToEmploymentDetails = navigateToEmploymentDetails
                        )
                    }
                } else {
                    items(10) { index ->
                        val isFirst = index == 0
                        val isLast = index == 9
                        EmploymentCard(
                            employment = null,
                            isFirst = isFirst,
                            isLast = isLast,
                            navigateToEmploymentDetails = navigateToEmploymentDetails
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun EmploymentCard(
    employment: Employment?,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    navigateToEmploymentDetails: (employmentId: Int) -> Unit
) {
    var layoutHeight by remember { mutableStateOf(0) }

    Row(
        Modifier.returningHeight { layoutHeight = it }
    ) {

        Breadcrumbs(
            height = with(LocalDensity.current) { layoutHeight.toDp() },
            isFirst = isFirst,
            isLast = isLast,
        )

        AnimatedCard(
            modifier = Modifier
                .padding(
                    top = if (isFirst) 0.dp else 8.dp,
                    bottom = if (isLast) 0.dp else 8.dp
                )
                .testTag("employmentCard${employment?.id ?: -1}"),
            onClick = {
                employment?.let {
                    navigateToEmploymentDetails(it.id)
                }
            },
        ) {
            Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(isLoading = employment == null)
                Spacer(modifier = Modifier.width(8.dp))
                Description(employment)
            }
        }
    }
}

@Composable
private fun Breadcrumbs(
    height: Dp,
    isFirst: Boolean = false,
    isLast: Boolean = false
) {
    val strokeWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val dotRadius = with(LocalDensity.current) { 4.dp.toPx() }

    Canvas(
        Modifier
            .width(32.dp)
            .height(height)
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            color = Color.LightGray,
            start = Offset(canvasWidth / 2, if (isFirst) canvasHeight / 2 else 0f),
            end = Offset(canvasWidth / 2, if (isLast) canvasHeight / 2 else canvasHeight),
            strokeWidth = strokeWidth
        )

        drawCircle(
            color = Color.LightGray,
            center = center,
            radius = dotRadius
        )
    }
}

@Composable
private fun Avatar(
    isLoading: Boolean
) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer()
            ),
        color = MaterialTheme.colors.primary
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            imageVector = Icons.Filled.Apartment,
            contentDescription = DecorativeImage,
            colorFilter = ColorFilter.tint(Color.White)
        )
    }
}

@Composable
private fun Description(
    employment: Employment?
) {
    Column {
        val sharedModifier = Modifier
            .placeholder(
                visible = employment == null,
                highlight = PlaceholderHighlight.shimmer()
            )
            .fillMaxWidth()

        Text(
            modifier = sharedModifier,
            text = employment?.employer.orEmpty(),
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = Modifier.height(2.dp))

        val period: String = employment?.let {
            employment.workPeriod.run {
                val resources = LocalContext.current.resources
                buildString {
                    if (years > 0) {
                        append(
                            resources.getQuantityString(
                                R.plurals.years,
                                years,
                                years
                            )
                        )
                        append(' ')
                    }
                    if (months > 0) {
                        append(
                            resources.getQuantityString(
                                R.plurals.months,
                                months,
                                months
                            )
                        )
                    }
                }
            }.trim()
        }.orEmpty()

        Text(
            modifier = sharedModifier,
            text = period,
            style = MaterialTheme.typography.body1
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = sharedModifier,
            text = employment?.jobTitle.orEmpty(),
            style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
        )
    }
}

private fun Modifier.returningHeight(onHeightMeasured: (Int) -> Unit) =
    layout { measurable, constraints ->
        val placeable = measurable.measure(constraints)
        onHeightMeasured(placeable.height)
        layout(placeable.width, placeable.height) {
            placeable.placeRelative(0, 0)
        }
    }

// region Preview
@Preview(
    name = "Content",
    widthDp = 320,
    heightDp = 680,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    name = "Content Dark",
    widthDp = 320,
    heightDp = 680,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Composable
fun PreviewContent() {
    val previewEmployments =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.let { now ->
            (1..3).map {
                Employment(
                    id = it,
                    jobTitle = "Software developer",
                    employer = "CMG Mobile Apps",
                    startDate = now.minus(DatePeriod(months = 3 + it)),
                    endDate = now.minus(DatePeriod(months = 1 - it)),
                    city = "Graz",
                    description = listOf(
                        "Founder",
                        "Solutions Architect"
                    )
                )
            }
        }
    Theme {
        ProvideWindowInsets {
            Content(UiState(data = previewEmployments), 0.dp, {})
        }
    }
}
