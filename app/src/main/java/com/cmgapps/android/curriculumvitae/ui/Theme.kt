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

package com.cmgapps.android.curriculumvitae.ui

import androidx.compose.foundation.Indication
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.LocalContentColor
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.cmgapps.common.curriculumvitae.components.amber200
import com.cmgapps.common.curriculumvitae.components.amber700
import com.cmgapps.common.curriculumvitae.components.darkSystemBars
import com.cmgapps.common.curriculumvitae.components.lightBlue200
import com.cmgapps.common.curriculumvitae.components.lightBlue500
import com.cmgapps.common.curriculumvitae.components.lightBlue700
import com.cmgapps.common.curriculumvitae.components.lightSystemBars
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = lightBlue200,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber200,
)

private val LightColorPalette = lightColors(
    primary = lightBlue500,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber700,
)

@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    lightSystemBarColor: Color = lightSystemBars,
    darkSystemBarColor: Color = darkSystemBars,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    val systemUiController = rememberSystemUiController()
    val systemBarsColor = if (darkTheme) darkSystemBarColor else lightSystemBarColor
    SideEffect {
        systemUiController.setSystemBarsColor(
            systemBarsColor,
            darkIcons = !darkTheme,
        )
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        content = content,
    )
}

@Composable
fun themedRipple(bounded: Boolean = true): Indication = rememberRipple(
    color = if (MaterialTheme.colors.isLight) MaterialTheme.colors.primary else LocalContentColor.current,
    bounded = bounded,
)
