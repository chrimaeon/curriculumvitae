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

import androidx.compose.ui.graphics.Color
import kotlin.math.max
import kotlin.math.min

val lightBlue200 = Color(0xFF81d4fa)
val lightBlue500 = Color(0xFF03a9f4)
val lightBlue700 = Color(0xFF0288d1)
val amber200 = Color(0xFFffe082)
val amber500 = Color(0xFFffca28)
val amber700 = Color(0xFFffa000)
val darkSystemBars = Color(0x40000000)
val lightSystemBars = Color(0xB3FFFFFF)

fun Color.darker(factor: Float): Color {
    return Color(
        alpha = this.alpha,
        red = min(1F, max(this.red * factor, 0F)),
        green = min(1F, max(this.green * factor, 0F)),
        blue = min(1F, max(this.blue * factor, 0f))
    )
}
