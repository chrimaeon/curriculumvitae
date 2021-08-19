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

package com.cmgapps.desktop.curriculumvitae.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.LocalContentColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerIcon
import androidx.compose.ui.input.pointer.pointerMoveFilter
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.CopyRightText
import com.cmgapps.common.curriculumvitae.GitHubLink
import com.cmgapps.desktop.curriculumvitae.Colors
import com.cmgapps.desktop.curriculumvitae.components.Icon
import java.awt.Desktop
import java.net.URI

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun Footer() {
    Surface(color = Colors.DarkGrey) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                Left()
                Right()
            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Left() {
    Column {
        Text(CopyRightText)
        Row(
            verticalAlignment = Alignment.Bottom
        ) {
            var hover by remember { mutableStateOf(false) }

            Icon("code-slash", contentDescription = "Coded", color = Colors.Blue)
            Text(" with ")
            Icon("heart-fill", contentDescription = "Love", color = Colors.Red)
            Text(" and ")
            Text(
                "Compose for Desktop",
                modifier = Modifier
                    .clickable {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop()
                                .browse(URI.create("https://github.com/jetbrains/compose-jb"))
                        }
                    }
                    .pointerIcon(PointerIcon.Hand)
                    .pointerMoveFilter(
                        onEnter = {
                            hover = true
                            false
                        },
                        onExit = {
                            hover = false
                            false
                        }
                    )
                    .alpha(if (hover) 0.8f else 1.0f),
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Right() {
    var hover by remember { mutableStateOf(false) }

    Icon(
        svgName = "github",
        modifier = Modifier
            .size(32.dp)
            .clickable {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI.create(GitHubLink))
                }
            }
            .pointerIcon(PointerIcon.Hand)
            .pointerMoveFilter(
                onEnter = {
                    hover = true
                    false
                },
                onExit = {
                    hover = false
                    false
                }
            ),
        alpha = if (hover) 0.8f else 1f,
        contentDescription = "Link to Github Repository"
    )
}
