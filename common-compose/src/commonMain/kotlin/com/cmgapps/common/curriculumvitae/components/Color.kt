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

package com.cmgapps.common.curriculumvitae.components

import androidx.compose.ui.graphics.Color

val lightBlue200 = Color(0xFF81D4FA)
val lightBlue500 = Color(0xFF03A9F4)
val lightBlue700 = Color(0xFF0288D1)
val amber200 = Color(0xFFFFE082)
val amber500 = Color(0xFFFFCA28)
val amber700 = Color(0xFFFFA000)
val red600 = Color(0xFFE53935)
val red800 = Color(0xFFC62828)
val blue900 = Color(0xFF0D47A1)
val blue400 = Color(0xFF42A5F5)

fun Color.darker(factor: Float): Color {
    return Color(
        alpha = this.alpha,
        red = (this.red * factor).coerceIn(0F, 1F),
        green = (this.green * factor).coerceIn(0F, 1F),
        blue = (this.blue * factor).coerceIn(0F, 1F),
    )
}
