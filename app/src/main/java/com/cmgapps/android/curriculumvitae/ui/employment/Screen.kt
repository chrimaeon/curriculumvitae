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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.UiEvent
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.infra.lifecycleAware
import com.cmgapps.android.curriculumvitae.util.ThemedPreview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.toPaddingValues
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefreshIndicator
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import timber.log.Timber
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
    onError: @Composable (String) -> Unit
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        val employmentUiState by viewModel.employments.lifecycleAware()
            .collectAsState(initial = UiState.Init)

        val uiEvent = viewModel.uiEvent

        if (uiEvent is UiEvent.Error) {
            onError(stringResource(id = uiEvent.resId))
        }

        when (employmentUiState) {
            UiState.Loading -> ContentLoading()
            is UiState.Error -> ContentError(
                error = (employmentUiState as UiState.Error).error,
                screenName = "EmploymentScreen"
            )
            is UiState.Success -> Content(
                employments = (employmentUiState as UiState.Success<List<Employment>>).data,
                uiEvent = uiEvent,
                bottomContentPadding = bottomContentPadding,
                navController = navController,
                onRefresh = {
                    viewModel.refresh()
                },
            )
            else -> if (BuildConfig.DEBUG) {
                Timber.tag(TAG).d(employmentUiState.javaClass.name)
            }
        }
    }
}

@Composable
private fun Content(
    employments: List<Employment>,
    uiEvent: UiEvent,
    bottomContentPadding: Dp,
    navController: NavController,
    onRefresh: () -> Unit = {},
) {
    var isRefreshing = false
    when (uiEvent) {
        UiEvent.IsRefreshing -> isRefreshing = true
        UiEvent.DoneRefreshing, is UiEvent.Error -> isRefreshing = false
        else -> {
            // ignore
        }
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
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
            contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
                bottom = false,
                additionalTop = 8.dp,
                additionalBottom = bottomContentPadding,
                additionalStart = 2.dp,
                additionalEnd = 2.dp
            )
        ) {
            items(employments, key = { it.id }) { employment ->
                EmploymentCard(employment = employment, navController = navController)
            }
        }
    }
}

@OptIn(ExperimentalTime::class)
@Composable
private fun EmploymentCard(employment: Employment, navController: NavController) {
    val resources = LocalContext.current.resources
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                navController.navigate(SubScreen.EmploymentDetail.routeWithId(employment.id))
            }
            .testTag("employmentCard${employment.id}"),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = employment.employer,
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )

            val period: String = Period.between(
                employment.startDate,
                employment.endDate ?: LocalDate.now()
            ).plusMonths(1).run {
                buildString {
                    if (years > 0) {
                        append(resources.getQuantityString(R.plurals.years, years, years))
                        append(' ')
                    }
                    if (months > 0) {
                        append(resources.getQuantityString(R.plurals.months, months, months))
                    }
                }
            }

            Text(
                text = period,
                style = MaterialTheme.typography.body1
            )
            Text(
                text = employment.jobTitle,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold)
            )
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
