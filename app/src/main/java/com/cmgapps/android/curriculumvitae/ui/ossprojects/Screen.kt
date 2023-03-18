/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.ui.ossprojects

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.PullRefreshDefaults
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.components.StarChip
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer

@Composable
fun OssProjectsScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: OssProjectsViewModel,
    snackbarHostState: SnackbarHostState,
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
            return ContentError()
        }

        Content(
            uiState = uiState,
            bottomContentPadding = bottomContentPadding,
            onRefresh = {
                viewModel.refresh()
            },
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun Content(
    uiState: UiState<List<OssProject>>,
    bottomContentPadding: Dp,
    onRefresh: () -> Unit = {},
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = uiState.loading,
        onRefresh = onRefresh,
        refreshingOffset = with(LocalDensity.current) {
            WindowInsets.statusBars.getTop(this).toDp() + PullRefreshDefaults.RefreshingOffset
        },
    )

    Box(
        modifier = Modifier.pullRefresh(state = pullRefreshState),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding =
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                .add(
                    WindowInsets(
                        left = 16.dp,
                        top = 8.dp,
                        right = 16.dp,
                        bottom = bottomContentPadding,
                    ),
                ).asPaddingValues(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val ossProjects = uiState.data
            if (ossProjects != null) {
                if (ossProjects.isNotEmpty()) {
                    items(
                        ossProjects,
                        key = { project -> project.name },
                    ) { project ->
                        OssProjectCard(
                            ossProject = project,
                        )
                    }
                } else {
                    items(10) {
                        OssProjectCard(
                            ossProject = null,
                        )
                    }
                }
            }
        }
        PullRefreshIndicator(
            refreshing = uiState.loading,
            state = pullRefreshState,
            contentColor = MaterialTheme.colors.secondaryVariant,
            modifier = Modifier.align(Alignment.TopCenter),
        )
    }
}

@Composable
private fun OssProjectCard(
    ossProject: OssProject?,
) {
    Card(
        modifier = Modifier
            .placeholder(
                visible = ossProject == null,
                highlight = PlaceholderHighlight.shimmer(),
            )
            .fillMaxWidth()
            .testTag("ossProjectCard${ossProject?.name ?: ""}"),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = ossProject?.name ?: "",
                    style = MaterialTheme.typography.h5,
                )
                StarChip(
                    modifier = Modifier.padding(start = 8.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    stars = ossProject?.stars ?: 0,
                )
            }

            Text(ossProject?.description ?: "")
        }
    }
}

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
private fun ContentPreview() {
    Theme {
        val previewProjects = listOf(
            OssProject(
                name = "my-project",
                description = "My Open Source Project",
                url = "https://cmgapps.com",
                topics = listOf("kotlin", "android", "kotlin-multiplatform"),
                stars = 42,
                private = false,
                fork = false,
                archived = false,
            ),
            OssProject(
                name = "my-project-with-a-very-long-name",
                description = "My Open Source Project",
                url = "https://cmgapps.com",
                topics = listOf("kotlin", "android", "kotlin-multiplatform"),
                stars = 42,
                private = false,
                fork = false,
                archived = false,
            ),
        )
        Content(uiState = UiState(data = previewProjects), bottomContentPadding = 0.dp)
    }
}
