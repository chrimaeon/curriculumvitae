/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.webcanvas.curriculumvitae

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
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
        content()
    }
}
