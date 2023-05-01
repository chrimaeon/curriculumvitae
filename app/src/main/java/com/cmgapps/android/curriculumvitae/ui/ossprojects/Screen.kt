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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
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
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    uiState: UiState<List<OssProject>>,
    bottomContentPadding: Dp,
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
    //     // modifier = Modifier.pullRefresh(state = pullRefreshState),
    // ) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(id = R.string.oss_projects_label))
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
        val layoutDirection = LocalLayoutDirection.current

        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                top = innerPadding.calculateTopPadding() + 8.dp,
                start = innerPadding.calculateStartPadding(layoutDirection) + 16.dp,
                bottom = bottomContentPadding,
                end = innerPadding.calculateEndPadding(layoutDirection) + 16.dp,
            ),
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
                    style = MaterialTheme.typography.headlineSmall,
                )
                StarChip(
                    modifier = Modifier.padding(start = 8.dp),
                    backgroundColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
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
