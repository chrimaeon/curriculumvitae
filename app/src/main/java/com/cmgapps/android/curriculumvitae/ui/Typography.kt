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

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import com.cmgapps.android.curriculumvitae.R

private val CrimsonPro = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.crimson_pro_extralight,
            weight = FontWeight.W200,
            style = FontStyle.Normal,
        ),
        Font(
            resId = R.font.crimson_pro_extralightitalic,
            weight = FontWeight.W200,
            style = FontStyle.Italic,
        ),
        Font(
            resId = R.font.crimson_pro_regular,
            weight = FontWeight.W400,
            style = FontStyle.Normal,
        ),
        Font(
            resId = R.font.crimson_pro_italic,
            weight = FontWeight.W400,
            style = FontStyle.Italic,
        ),
        Font(
            resId = R.font.crimson_pro_bold,
            weight = FontWeight.W600,
            style = FontStyle.Normal,
        ),
        Font(
            resId = R.font.crimson_pro_bolditalic,
            weight = FontWeight.W600,
            style = FontStyle.Italic,
        ),
    ),
)

private val defaultTypography = androidx.compose.material3.Typography()
val Typography = androidx.compose.material3.Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = CrimsonPro),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = CrimsonPro),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = CrimsonPro),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = CrimsonPro),
    headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = CrimsonPro),
    headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = CrimsonPro),
)
