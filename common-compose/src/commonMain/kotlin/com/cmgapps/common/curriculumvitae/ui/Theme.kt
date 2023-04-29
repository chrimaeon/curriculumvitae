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

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.platform.Font
import com.cmgapps.common.curriculumvitae.components.amber200
import com.cmgapps.common.curriculumvitae.components.amber700
import com.cmgapps.common.curriculumvitae.components.lightBlue200
import com.cmgapps.common.curriculumvitae.components.lightBlue500
import com.cmgapps.common.curriculumvitae.components.lightBlue700
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.resource

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

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val fonts by produceState<List<ByteArray>?>(null) {
        val light = async { resource("font/Roboto-Light.ttf").readBytes() }
        val lightItalic = async { resource("font/Roboto-LightItalic.ttf").readBytes() }
        val regular = async { resource("font/Roboto-Regular.ttf").readBytes() }
        val regularItalic = async { resource("font/Roboto-Italic.ttf").readBytes() }
        val medium = async { resource("font/Roboto-Medium.ttf").readBytes() }
        val mediumItalic = async { resource("font/Roboto-MediumItalic.ttf").readBytes() }

        value = awaitAll(light, lightItalic, regular, regularItalic, medium, mediumItalic)
    }

    val fontFamily = fonts?.let {
        FontFamily(
            Font(
                identity = "Roboto-Light",
                data = it[0],
                weight = FontWeight.Light,
                style = FontStyle.Normal,
            ),
            Font(
                identity = "Roboto-LightItalic",
                data = it[1],
                weight = FontWeight.Light,
                style = FontStyle.Italic,
            ),
            Font(
                identity = "Roboto-Regular",
                data = it[2],
                weight = FontWeight.Normal,
                style = FontStyle.Normal,
            ),
            Font(
                identity = "Roboto-RegularItalic",
                data = it[3],
                weight = FontWeight.Normal,
                style = FontStyle.Italic,
            ),
            Font(
                identity = "Roboto-Medium",
                data = it[4],
                weight = FontWeight.Medium,
                style = FontStyle.Normal,
            ),
            Font(
                identity = "Roboto-MediumItalic",
                data = it[5],
                weight = FontWeight.Medium,
                style = FontStyle.Normal,
            ),
        )
    } ?: FontFamily.SansSerif

    MaterialTheme(
        colors = if (darkTheme) DarkColorPalette else LightColorPalette,
        typography = Typography(
            defaultFontFamily = fontFamily,
        ),
    ) {
        content()
    }
}
