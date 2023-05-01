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

package com.cmgapps.android.curriculumvitae.ui.employment.detail

import android.content.res.Configuration
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.components.darker
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import me.onebone.toolbar.CollapsingToolbarScaffold
import me.onebone.toolbar.CollapsingToolbarScaffoldState
import me.onebone.toolbar.CollapsingToolbarScope
import me.onebone.toolbar.ScrollStrategy
import me.onebone.toolbar.rememberCollapsingToolbarScaffoldState
import androidx.compose.ui.graphics.lerp as lerpGraphics

@Composable
fun EmploymentDetails(
    viewModel: EmploymentDetailViewModel,
    modifier: Modifier = Modifier,
    navigateUp: () -> Unit,
) {
    val uiState = viewModel.uiState
    val employment = uiState.data

    Box(modifier = modifier.fillMaxSize()) {
        when {
            uiState.loading -> ContentLoading()
            employment != null -> EmploymentDetails(
                employment = employment,
                navigateUp = navigateUp,
            )

            uiState.exception != null -> ContentError()
        }
    }
}

private const val numCircles = 4
private fun getCircleColorFraction(circle: Int) =
    0.5f + (circle.toFloat() / (numCircles + 1)) * (0.9f - 0.5f)

@Composable
private fun EmploymentDetails(employment: Employment, navigateUp: () -> Unit) {
    val state = rememberCollapsingToolbarScaffoldState()
    val headerColor = MaterialTheme.colorScheme.secondary

    Surface(color = headerColor.darker(getCircleColorFraction(4))) {
        CollapsingToolbarScaffold(
            modifier = Modifier.fillMaxSize(),
            state = state,
            toolbar = {
                TopBar(
                    title = employment.jobTitle,
                    headerColor = headerColor,
                    state = state,
                    navigateUp = navigateUp,
                )
            },
            scrollStrategy = ScrollStrategy.EnterAlwaysCollapsed,
        ) {
            val cornerRadius =
                lerp(16.dp, 0.dp, ((1 - state.toolbarState.progress) * 3).coerceIn(0.0f, 1.0f))
            LazyColumn(
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                    )
                    .fillMaxSize(),
                contentPadding = WindowInsets.navigationBars.add(
                    WindowInsets(left = 16.dp, top = 16.dp, right = 16.dp),
                ).asPaddingValues(),
            ) {
                item {
                    Text(
                        text = employment.employer,
                    )
                }
                item {
                    Text(
                        text = employment.workPeriod.asHumanReadableString(),
                    )
                }
                item {
                    Text(
                        text = employment.description.joinToString(separator = "\n\n"),
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CollapsingToolbarScope.TopBar(
    title: String,
    headerColor: Color,
    state: CollapsingToolbarScaffoldState,
    navigateUp: () -> Unit,
) {
    val contentColor = MaterialTheme.colorScheme.contentColorFor(headerColor)

    BoxWithConstraints(
        modifier = Modifier
            .parallax()
            .fillMaxWidth(),
    ) {
        val headerHeight = if (minWidth > 600.dp) 136.dp else minWidth * (9F / 16F)
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerHeight)
                .background(headerColor)
                .clipToBounds(),
        ) {
            val canvasWidth = size.width
            val canvasHeight = size.height

            for (i in numCircles downTo 1) {
                drawCircle(
                    color = headerColor.darker(getCircleColorFraction(i)),
                    center = Offset(x = canvasWidth / 2, y = canvasHeight),
                    radius = (canvasWidth / 2) / numCircles * i,
                )
            }
        }
    }

    val collapsedTextOffset = with(LocalDensity.current) {
        IntOffset((72.dp - 16.dp).toPx().toInt(), 0)
    }

    val backgroundColor = lerpGraphics(
        headerColor,
        headerColor.copy(alpha = 0.0f),
        (state.toolbarState.progress * 3).coerceIn(0.0f, 1.0f),
    )

    TopAppBar(
        modifier = Modifier.statusBarsPadding(),
        colors = TopAppBarDefaults.largeTopAppBarColors(
            containerColor = backgroundColor,
            titleContentColor = contentColor,
            navigationIconContentColor = contentColor,
        ),
        title = { },
        navigationIcon = {
            IconButton(
                onClick = { navigateUp() },
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_up),
                )
            }
        },
    )

    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge.copy(color = contentColor),
        fontSize = lerp(
            MaterialTheme.typography.titleLarge.fontSize,
            MaterialTheme.typography.headlineMedium.fontSize,
            state.toolbarState.progress,
        ),
        modifier = Modifier
            .road(
                whenExpanded = Alignment.BottomStart,
                whenCollapsed = { _, _, _ -> collapsedTextOffset },
            )
            .statusBarsPadding()
            .padding(16.dp),
    )
}

// region Preview
@Preview(name = "Content", widthDp = 320, heightDp = 680)
@Preview(
    name = "Content Dark",
    widthDp = 320,
    heightDp = 680,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
@Preview(name = "Content Land", widthDp = 680, heightDp = 320)
@Composable
fun PreviewEmploymentDetails() {
    val previewEmployment = Employment(
        12,
        "Job Title",
        "Employer",
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date,
        null,
        "Graz",
        listOf("Line 1"),
    )
    Theme {
        EmploymentDetails(previewEmployment) {}
    }
}
// endregion
