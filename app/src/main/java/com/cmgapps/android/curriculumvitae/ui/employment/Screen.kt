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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmgapp.shared.curriculumvitae.data.Employment
import com.cmgapps.android.compomaeon.ui.Theme
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.infra.Resource
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import dev.chrisbanes.accompanist.insets.toPaddingValues
import java.util.Date

@Composable
fun EmploymentScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: EmploymentViewModel
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        val employments by viewModel.employment.observeAsState(initial = Resource.Loading)

        when (employments) {
            is Resource.Loading -> ContentLoading()
            is Resource.Error -> ContentError((employments as Resource.Error).error)
            is Resource.Success -> Content(
                (employments as Resource.Success<List<Employment>>).data,
                bottomContentPadding
            )
        }
    }
}

@Composable
fun Content(employments: List<Employment>, bottomContentPadding: Dp) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.Top),
        contentPadding = LocalWindowInsets.current.systemBars.toPaddingValues(
            bottom = false,
            additionalTop = 8.dp,
            additionalBottom = bottomContentPadding
        )
    ) {
        items(employments) { employment ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colors.primary,
                elevation = 1.dp
            ) {
                Text(
                    text = employment.jobTitle,
                    style = MaterialTheme.typography.h3
                )
            }
        }
    }
}

// region Preview
@Preview
@Composable
fun PreviewContent() {
    Theme {
        ProvideWindowInsets {
            Content(
                employments = listOf(
                    Employment(
                        jobTitle = "Software developer",
                        employer = "CMG Mobile Apps",
                        startDate = Date(),
                        endDate = null,
                        city = "Graz",
                        description = listOf(
                            "Founder",
                            "Solutions Architect"
                        )
                    )
                ),
                bottomContentPadding = 0.dp
            )
        }
    }
}
// endregion
