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

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.material.Colors
import androidx.compose.ui.graphics.Color
import com.cmgapps.common.curriculumvitae.components.blue400
import com.cmgapps.common.curriculumvitae.components.blue900
import com.cmgapps.common.curriculumvitae.components.red600
import com.cmgapps.common.curriculumvitae.components.red800

val Colors.heartRed: Color
    get() = if (isLight) red800 else red600

val Colors.codeBlue: Color
    get() = if (isLight) blue900 else blue400
