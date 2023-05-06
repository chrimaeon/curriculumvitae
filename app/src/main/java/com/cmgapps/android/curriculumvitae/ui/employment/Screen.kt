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
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.android.curriculumvitae.ui.avatarBlue
import com.cmgapps.android.curriculumvitae.ui.avatarGreen
import com.cmgapps.android.curriculumvitae.ui.avatarOrange
import com.cmgapps.android.curriculumvitae.ui.avatarRed
import com.cmgapps.android.curriculumvitae.ui.avatarViolet
import com.cmgapps.android.curriculumvitae.ui.onAvatarBlue
import com.cmgapps.android.curriculumvitae.ui.onAvatarGreen
import com.cmgapps.android.curriculumvitae.ui.onAvatarOrange
import com.cmgapps.android.curriculumvitae.ui.onAvatarRed
import com.cmgapps.android.curriculumvitae.ui.onAvatarViolet
import com.cmgapps.common.curriculumvitae.components.AnimatedCard
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import kotlinx.datetime.Clock
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.toLocalDateTime
import java.time.format.TextStyle
import java.util.Locale

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
        contentAlignment = Alignment.Center,
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: UiState<List<Employment>?>,
    bottomContentPadding: Dp,
    navigateToEmploymentDetails: (employmentId: Int) -> Unit,
    onRefresh: () -> Unit = {},
) {
    // TODO pull-to-refresh with Material 3
    // val pullRefreshState = rememberPullRefreshState(
    //     refreshing = uiState.loading,
    //     onRefresh = onRefresh,
    //     refreshingOffset = with(LocalDensity.current) {
    //         WindowInsets.statusBars.getTop(this).toDp() + PullRefreshDefaults.RefreshingOffset
    //     },
    // )
    // Box(
    //     modifier = Modifier.pullRefresh(state = pullRefreshState),
    // ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.employment_label))
                },
                actions = {
                    IconButton(onClick = onRefresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                        )
                    }
                },
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                top = 8.dp,
                start = 2.dp,
                bottom = bottomContentPadding,
                end = 16.dp,
            ),
            // WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
            //     .add(
            //         WindowInsets(
            //             left = 2.dp,
            //             top = 8.dp,
            //             right = 16.dp,
            //             bottom = bottomContentPadding,
            //         ),
            //     ).asPaddingValues(),
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
                            navigateToEmploymentDetails = navigateToEmploymentDetails,
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
                            navigateToEmploymentDetails = navigateToEmploymentDetails,
                        )
                    }
                }
            }
        }
        // TODO pull-to-refresh with Material 3
        // PullRefreshIndicator(
        //     refreshing = uiState.loading,
        //     state = pullRefreshState,
        //     contentColor = MaterialTheme.colors.secondaryVariant,
        //     modifier = Modifier.align(Alignment.TopCenter),
        // )
    }
}

@Composable
private fun EmploymentCard(
    employment: Employment?,
    isFirst: Boolean = false,
    isLast: Boolean = false,
    navigateToEmploymentDetails: (employmentId: Int) -> Unit,
) {
    var layoutHeight by remember { mutableStateOf(0) }

    Row(
        Modifier.returningHeight { layoutHeight = it },
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
                    bottom = if (isLast) 0.dp else 8.dp,
                )
                .testTag("employmentCard${employment?.id ?: -1}"),
            onClick = {
                employment?.let {
                    navigateToEmploymentDetails(it.id)
                }
            },
            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
        ) {
            CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Avatar(isLoading = employment == null, employerName = employment?.employer)
                    Spacer(modifier = Modifier.width(8.dp))
                    Description(employment)
                }
            }
        }
    }
}

@Composable
private fun Breadcrumbs(
    height: Dp,
    isFirst: Boolean = false,
    isLast: Boolean = false,
) {
    val strokeWidth = with(LocalDensity.current) { 1.dp.toPx() }
    val dotRadius = with(LocalDensity.current) { 4.dp.toPx() }
    val color = MaterialTheme.colorScheme.onBackground

    Canvas(
        Modifier
            .width(32.dp)
            .height(height),
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height

        drawLine(
            color = color,
            start = Offset(canvasWidth / 2, if (isFirst) canvasHeight / 2 else 0f),
            end = Offset(canvasWidth / 2, if (isLast) canvasHeight / 2 else canvasHeight),
            strokeWidth = strokeWidth,
        )

        drawCircle(
            color = color,
            center = center,
            radius = dotRadius,
        )
    }
}

@Composable
private fun rememberAvatarColors(colorScheme: ColorScheme): List<Pair<Color, Color>> {
    return remember {
        listOf(
            colorScheme.avatarRed to colorScheme.onAvatarRed,
            colorScheme.avatarOrange to colorScheme.onAvatarOrange,
            colorScheme.avatarGreen to colorScheme.onAvatarGreen,
            colorScheme.avatarBlue to colorScheme.onAvatarBlue,
            colorScheme.avatarViolet to colorScheme.onAvatarViolet,
        )
    }
}

@Composable
private fun Avatar(
    employerName: String?,
    isLoading: Boolean,
) {
    val avatarColors = rememberAvatarColors(MaterialTheme.colorScheme)
    val (backgroundColor, foregroundColor) = remember(employerName) {
        val hashCode =
            employerName?.splitToSequence(' ')?.mapNotNull { it.firstOrNull() }?.joinToString()
                .hashCode()
        avatarColors[hashCode % avatarColors.size]
    }

    Surface(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .placeholder(
                visible = isLoading,
                highlight = PlaceholderHighlight.shimmer(),
            ),
        color = backgroundColor,
    ) {
        Image(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            imageVector = Icons.Filled.Apartment,
            contentDescription = DecorativeImage,
            colorFilter = ColorFilter.tint(foregroundColor),
        )
    }
}

@Composable
private fun Description(
    employment: Employment?,
) {
    Column {
        val sharedModifier = Modifier
            .placeholder(
                visible = employment == null,
                highlight = PlaceholderHighlight.shimmer(),
            )
            .fillMaxWidth()

        Text(
            modifier = sharedModifier,
            text = employment?.employer.orEmpty(),
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
        )

        Spacer(modifier = Modifier.height(2.dp))

        Row(
            modifier = sharedModifier,
        ) {
            CompositionLocalProvider(
                LocalTextStyle provides MaterialTheme.typography.bodySmall,
            ) {
                Text(
                    modifier = Modifier.alignBy(LastBaseline),
                    text = employment?.workPeriod?.asHumanReadableString().orEmpty(),
                )
                Text(
                    modifier = Modifier
                        .padding(horizontal = 2.dp)
                        .alignBy(LastBaseline),
                    text = "\u00B7",
                )
                val startEnd = employment?.let {
                    buildString {
                        append(
                            it.startDate.month.getDisplayName(
                                TextStyle.SHORT,
                                Locale.getDefault(),
                            ),
                        )
                        append("\u00A0")
                        append(it.startDate.year)

                        append(" - ")

                        it.endDate?.let { date ->
                            append(date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                            append("\u00A0")
                            append(date.year)
                        } ?: append(stringResource(R.string.present))
                    }
                }.orEmpty()
                Text(
                    modifier = Modifier.alignBy(LastBaseline),
                    text = startEnd,
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            modifier = sharedModifier,
            text = employment?.jobTitle.orEmpty(),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
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
    backgroundColor = 0xFFFFFFFF,
)
@Preview(
    name = "Content Dark",
    widthDp = 320,
    heightDp = 680,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
@Composable
fun PreviewContent() {
    val previewEmployments =
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date.let { now ->
            (1..5).map {
                Employment(
                    id = it,
                    jobTitle = "Software developer",
                    employer = (96 + it).toChar().titlecase(),
                    startDate = now.minus(DatePeriod(months = 3 + it)),
                    endDate = now.minus(DatePeriod(months = 1 - it)),
                    city = "Graz",
                    description = listOf(
                        "Founder",
                        "Solutions Architect",
                    ),
                )
            }
        }
    Theme {
        Content(UiState(data = previewEmployments), 0.dp, {})
    }
}
