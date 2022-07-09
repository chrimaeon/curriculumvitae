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

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.components.amber200
import com.cmgapps.common.curriculumvitae.components.amber700
import com.cmgapps.common.curriculumvitae.components.lightBlue200
import com.cmgapps.common.curriculumvitae.components.lightBlue500
import com.cmgapps.common.curriculumvitae.components.lightBlue700

private val DarkColorPalette = darkColors(
    primary = lightBlue200,
    onPrimary = Color.Black,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber200,
)

private val LightColorPalette = lightColors(
    primary = lightBlue500,
    onPrimary = Color.Black,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber700,
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
    ) {
        CompositionLocalProvider(
            LocalScrollbarStyle provides ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 8.dp,
                shape = MaterialTheme.shapes.small,
                hoverDurationMillis = 300,
                unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f),
            ),
        ) {
            content()
        }
    }
}
