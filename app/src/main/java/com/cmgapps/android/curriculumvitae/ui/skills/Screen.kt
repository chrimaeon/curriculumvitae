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

package com.cmgapps.android.curriculumvitae.ui.skills

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.common.curriculumvitae.components.TagCloud
import com.cmgapps.common.curriculumvitae.data.domain.Skill

@Composable
fun SkillsScreen(
    viewModel: SkillsViewModel,
    snackbarHostState: SnackbarHostState
) {
    val uiState = viewModel.uiState

    if (uiState.loading && uiState.data == null) {
        ContentLoading()
    }

    if (uiState.networkError) {
        val errorMessage = stringResource(id = R.string.refresh_error)
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    if (uiState.exception != null && !uiState.networkError && uiState.data == null) {
        ContentError()
    }

    uiState.data?.let {
        Content(skills = it)
    }
}

@Composable
private fun Content(skills: List<Skill>) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        TagCloud {
            skills.forEach { skill ->
                Text(
                    skill.name,
                    style = skill.levelAsTextStyle()
                )
            }
        }
    }
}

@Composable
private fun Skill.levelAsTextStyle(): TextStyle = when (level) {
    1 -> MaterialTheme.typography.h6
    2 -> MaterialTheme.typography.h5
    3 -> MaterialTheme.typography.h4
    4 -> MaterialTheme.typography.h3
    5 -> MaterialTheme.typography.h2
    else -> error("level not specified")
}.copy(fontWeight = FontWeight.Bold)

@Preview(
    widthDp = 320,
    heightDp = 680,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewSkills() {
    val skills = listOf(
        Skill(
            "Foobar",
            1
        ),
        Skill(
            "Foobar",
            2
        ),
        Skill(
            "Foobar",
            3
        ),
        Skill(
            "Foobar",
            4
        ),
        Skill(
            "Foobar",
            5
        ),
        Skill(
            "Foobar",
            1
        ),
        Skill(
            "Foobar",
            3
        ),
        Skill(
            "Foobar",
            2
        )
    )
    MaterialTheme {
        Content(skills)
    }
}
