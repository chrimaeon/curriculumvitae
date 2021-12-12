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

package com.cmgapps.wear.curriculumvitae.ui

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.wear.compose.material.Colors
import androidx.wear.compose.material.MaterialTheme

private val DarkColorPalette = Colors(
    primary = lightBlue200,
    primaryVariant = lightBlue700,
    secondary = amber200,
    secondaryVariant = amber200,
)

@Composable
fun Theme(
    content: @Composable () -> Unit
) {
    Log.d("Theme", DarkColorPalette.secondary.toArgb().toUInt().toString(16))
    MaterialTheme(
        colors = DarkColorPalette,
        content = content
    )
}
