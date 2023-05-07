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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EmploymentDetails(employment: Employment, navigateUp: () -> Unit) {
    val headerColor = MaterialTheme.colorScheme.surfaceVariant
    val contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        containerColor = headerColor,
        contentColor = MaterialTheme.colorScheme.onSurface,
        topBar = {
            LargeTopAppBar(
                title = {
                    Text(
                        employment.jobTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                colors = TopAppBarDefaults.largeTopAppBarColors(
                    containerColor = headerColor,
                    titleContentColor = contentColor,
                    navigationIconContentColor = contentColor,
                ),
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
                scrollBehavior = scrollBehavior,
            )
        },
    ) { innerPadding ->
        val scrollState = remember(scrollBehavior) { scrollBehavior.state }

        val initialCornerRadius = 16.dp

        var cornerRadius by remember { mutableStateOf(initialCornerRadius) }

        LaunchedEffect(scrollState) {
            snapshotFlow { scrollState.collapsedFraction }
                .distinctUntilChanged()
                .collect {
                    cornerRadius =
                        lerp(initialCornerRadius, 0.dp, it)
                }
        }

        val layoutDirection = LocalLayoutDirection.current

        LazyColumn(
            modifier = Modifier
                .padding(
                    PaddingValues(
                        start = innerPadding.calculateStartPadding(layoutDirection),
                        top = innerPadding.calculateTopPadding(),
                        end = innerPadding.calculateEndPadding(layoutDirection),
                    ),
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
                )
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 16.dp,
                end = 16.dp,
                bottom = innerPadding.calculateBottomPadding(),
            ),
            verticalArrangement = Arrangement.spacedBy(16.dp),
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
                val descriptions = employment.description

                descriptions.forEachIndexed { index, description ->
                    Text(description)
                    if (index != descriptions.lastIndex) {
                        Box(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
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
