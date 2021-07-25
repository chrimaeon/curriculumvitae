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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.data.domain.Employment
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.ui.darker
import com.cmgapps.android.curriculumvitae.ui.lightBlue500
import com.cmgapps.android.curriculumvitae.util.ThemedPreview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import timber.log.Timber
import java.time.LocalDate

@Composable
fun EmploymentDetails(
    viewModel: EmploymentDetailViewModel,
    modifier: Modifier,
    navigateUp: () -> Unit,
) {
    val employment by viewModel.employment.collectAsState(initial = UiState.Init)

    Box(modifier = modifier.fillMaxSize()) {
        when (employment) {
            UiState.Loading -> ContentLoading()
            is UiState.Success -> EmploymentDetails(
                employment = (employment as UiState.Success<Employment>).data,
                navigateUp = navigateUp,
            )
            is UiState.Error -> ContentError(
                error = (employment as UiState.Error).error,
                screenName = "EmploymentDetails",
            )
            else -> if (BuildConfig.DEBUG) {
                Timber.tag("EmploymentDetails").d(employment.javaClass.simpleName)
            }
        }
    }
}

@Composable
private fun EmploymentDetails(employment: Employment, navigateUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            val headerColor = lightBlue500
            val headerHeight = if (minWidth > 600.dp) 100.dp else minWidth * (9F / 16F)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .background(headerColor)
                    .clipToBounds()
            ) {
                val canvasWidth = size.width
                val canvasHeight = size.height
                val numCircles = 4
                for (i in numCircles downTo 1) {
                    drawCircle(
                        color = headerColor.darker(0.5f + (0.9f - 0.5f) / (numCircles + 1) * i),
                        center = Offset(x = canvasWidth / 2, y = canvasHeight),
                        radius = (canvasWidth / 2) / numCircles * i
                    )
                }
            }
            IconButton(
                modifier = Modifier.padding(
                    rememberInsetsPaddingValues(
                        insets = LocalWindowInsets.current.statusBars,
                        additionalTop = 8.dp
                    )
                ),
                onClick = { navigateUp() }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(id = R.string.navigate_up),
                    tint = Color.White
                )
            }
        }
        Column {
            Text(
                text = employment.jobTitle
            )

            Text(
                text = employment.description.joinToString(separator = "\n\n")
            )
        }
    }
}

// region Preview

private val employment = Employment(
    12,
    "Job Title",
    "Employer",
    LocalDate.now(),
    null,
    "Graz",
    listOf("Line 1")
)

@Preview(name = "Content", widthDp = 320, heightDp = 680)
@Composable
fun PreviewEmploymentDetails() {
    ThemedPreview {
        EmploymentDetails(employment) {}
    }
}

@Preview(name = "Content Dark", widthDp = 320, heightDp = 680)
@Composable
fun PreviewEmploymentDetailsDark() {
    ThemedPreview(darkTheme = true) {
        EmploymentDetails(employment) {}
    }
}

@Preview(name = "Content Land", widthDp = 680, heightDp = 320)
@Composable
fun PreviewEmploymentDetailsLandscape() {
    ThemedPreview(darkTheme = true) {
        EmploymentDetails(employment) {}
    }
}
// endregion
