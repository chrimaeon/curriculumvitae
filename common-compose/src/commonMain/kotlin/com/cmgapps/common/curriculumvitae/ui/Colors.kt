/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.ui

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
