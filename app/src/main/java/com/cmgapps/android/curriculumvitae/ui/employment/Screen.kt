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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.AnimatedCard
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.UiEvent
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.infra.lifecycleAware
import com.cmgapps.android.curriculumvitae.util.ThemedPreview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.time.LocalDate
import java.time.Period
import kotlin.time.ExperimentalTime

private const val TAG = "EmploymentScreen"

@Composable
fun EmploymentScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: EmploymentViewModel,
    navController: NavController,
    snackbarHostState: SnackbarHostState
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val employmentUiState by viewModel.employments.lifecycleAware()
            .collectAsState(initial = UiState.Init)

        val uiEvent = viewModel.uiEvent

        if (uiEvent is UiEvent.Error) {
            val errorMessage = stringResource(id = uiEvent.resId)
            LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(errorMessage)
            }
        }

        if (employmentUiState is UiState.Error) {
            ContentError(
                error = (employmentUiState as UiState.Error).error,
                screenName = "EmploymentScreen"
            )
        } else {
            @Suppress("UnnecessaryVariable")
            val employments = when (val state = employmentUiState) {
                is UiState.Success -> state.data
                is UiState.Loading -> emptyList()
                else -> null
            }

            Content(
                employments = employments,
                uiEvent = uiEvent,
                bottomContentPadding = bottomContentPadding,
                navController = navController,
                onRefresh = {
                    viewModel.refresh()
                },
            )
        }
    }
}

@Composable
private fun Content(
    employments: List<Employment>?,
    uiEvent: UiEvent,
    bottomContentPadding: Dp,
    navController: NavController,
    onRefresh: () -> Unit = {},
) {
    val isRefreshing = when (uiEvent) {
        UiEvent.IsRefreshing -> true
        else -> false
    }

    SwipeRefresh(
        state = rememberSwipeRefreshState(isRefreshing),
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
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Top),
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.systemBars,
                applyBottom = false,
                additionalStart = 2.dp,
                additionalTop = 8.dp,
                additionalEnd = 2.dp,
                additionalBottom = bottomContentPadding
            )
        ) {
            if (employments != null) {
                if (employments.isNotEmpty()) {
                    items(employments, key = { it.id }) { employment ->
                        EmploymentCard(employment = employment, navController = navController)
                    }
                } else {
                    items(10) {
                        EmploymentCard(employment = null, navController = navController)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun EmploymentCard(employment: Employment?, navController: NavController?) {

    AnimatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("employmentCard${employment?.id ?: 1}"),
        onClick = {
            employment?.let {
                navController?.navigate(SubScreen.EmploymentDetail.routeWithId(it.id))
            }
        },
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .placeholder(
                        visible = employment == null,
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

            Spacer(modifier = Modifier.width(8.dp))

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

                Spacer(modifier = Modifier.height(4.dp))

                val period: String = employment?.let {
                    Period.between(
                        employment.startDate,
                        employment.endDate ?: LocalDate.now()
                    ).plusMonths(1).run {
                        val resources = LocalContext.current.resources
                        buildString {
                            if (years > 0) {
                                append(resources.getQuantityString(R.plurals.years, years, years))
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
    }
}

// region Preview

private val employments = listOf(
    Employment(
        id = 1,
        jobTitle = "Software developer",
        employer = "CMG Mobile Apps",
        startDate = LocalDate.now().minusMonths(3),
        endDate = LocalDate.now().minusMonths(1),
        city = "Graz",
        description = listOf(
            "Founder",
            "Solutions Architect"
        )
    )
)

@Preview(name = "Content", widthDp = 320, heightDp = 680)
@Composable
fun PreviewContent() {
    ThemedPreview {
        ProvideWindowInsets {
            Content(employments, UiEvent.Init, 0.dp, rememberNavController())
        }
    }
}

@Preview(name = "Content Dark", widthDp = 320, heightDp = 680)
@Composable
fun PreviewContentDark() {
    ThemedPreview(darkTheme = true) {
        ProvideWindowInsets {
            Content(employments, UiEvent.Init, 0.dp, rememberNavController())
        }
    }
}
// endregion
