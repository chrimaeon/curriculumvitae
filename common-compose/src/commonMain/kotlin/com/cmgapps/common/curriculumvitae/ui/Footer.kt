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

package com.cmgapps.common.curriculumvitae.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.ui.icon.CodeSlash
import com.cmgapps.common.curriculumvitae.ui.icon.CvIcons
import com.cmgapps.common.curriculumvitae.ui.icon.Github
import com.cmgapps.common.curriculumvitae.ui.icon.HeartFill

@Composable
fun Footer(
    copyRightText: String,
    onComposeDesktopClick: () -> Unit,
    onGithubClick: () -> Unit,
) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
    ) {
        val content = remember {
            movableContentOf {
                CompositionLocalProvider(
                    LocalContentColor provides MaterialTheme.colors.contentColorFor(
                        MaterialTheme.colors.primarySurface,
                    ),
                ) {
                    Left(
                        onComposeDesktopClick = onComposeDesktopClick,
                        copyRightText = copyRightText,
                    )
                    Right(onGithubClick = onGithubClick)
                }
            }
        }
        BoxWithConstraints {
            if (maxWidth < 600.dp) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    content()
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(vertical = 10.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    content()
                }
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Left(
    copyRightText: String,
    onComposeDesktopClick: () -> Unit,
) {
    Column {
        Text(copyRightText)
        Row(
            verticalAlignment = Alignment.Bottom,
        ) {
            var hover by remember { mutableStateOf(false) }

            Image(
                CvIcons.CodeSlash,
                contentDescription = "Coded",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.codeBlue),
            )
            Text(" with ")
            Image(
                CvIcons.HeartFill,
                contentDescription = "Love",
                colorFilter = ColorFilter.tint(MaterialTheme.colors.heartRed),
            )
            Text(" and ")
            Text(
                "Compose for Desktop",
                modifier = Modifier
                    .clickable(onClick = onComposeDesktopClick)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .onPointerEvent(
                        PointerEventType.Enter,
                        onEvent = {
                            hover = true
                        },
                    )
                    .onPointerEvent(
                        PointerEventType.Exit,
                        onEvent = {
                            hover = false
                        },
                    )
                    .alpha(if (hover) 0.8f else 1.0f),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Right(
    onGithubClick: () -> Unit,
) {
    var hover by remember { mutableStateOf(false) }

    Image(
        CvIcons.Github,
        modifier = Modifier
            .size(32.dp)
            .clickable(onClick = onGithubClick)
            .pointerHoverIcon(PointerIcon.Hand)
            .onPointerEvent(
                PointerEventType.Enter,
                onEvent = {
                    hover = true
                },
            )
            .onPointerEvent(
                PointerEventType.Exit,
                onEvent = {
                    hover = false
                },
            ),
        alpha = if (hover) 0.8f else 1f,
        contentDescription = "Link to Github Repository",
    )
}
