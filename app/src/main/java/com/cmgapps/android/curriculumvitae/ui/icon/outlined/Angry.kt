/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.ui.icon.outlined

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.ui.icon.CvIcons

val CvIcons.Outlined.Angry: ImageVector
    get() {
        if (_icAngry != null) {
            return _icAngry!!
        }
        _icAngry = Builder(
            name = "Outlined.Angry",
            defaultWidth = 100.0.dp,
            defaultHeight = 100.0.dp,
            viewportWidth = 100.0f,
            viewportHeight = 100.0f,
        ).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(50.0f, 5.0f)
                arcTo(45.0f, 45.0f, 0.0f, true, true, 5.0f, 50.0f)
                arcTo(45.05f, 45.05f, 0.0f, false, true, 50.0f, 5.0f)
                moveToRelative(0.0f, -5.0f)
                arcToRelative(50.0f, 50.0f, 0.0f, true, false, 50.0f, 50.0f)
                arcTo(50.0f, 50.0f, 0.0f, false, false, 50.0f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(56.8f, 46.42f)
                arcToRelative(6.26f, 8.95f, 0.0f, true, false, 12.52f, 0.0f)
                arcToRelative(6.26f, 8.95f, 0.0f, true, false, -12.52f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(73.81f, 36.31f)
                curveToRelative(-5.36f, 0.38f, -8.71f, 2.57f, -13.07f, 4.91f)
                arcToRelative(41.14f, 41.14f, 0.0f, false, true, -5.41f, 2.43f)
                arcToRelative(11.32f, 11.32f, 0.0f, false, true, 6.26f, -10.83f)
                curveToRelative(4.0f, -2.34f, 10.54f, -1.24f, 12.22f, 3.49f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(31.72f, 76.0f)
                curveToRelative(8.72f, -11.6f, 27.92f, -11.73f, 36.56f, 0.0f)
                arcTo(76.6f, 76.6f, 0.0f, false, false, 59.0f, 73.24f)
                arcToRelative(39.2f, 39.2f, 0.0f, false, false, -18.0f, 0.0f)
                curveToRelative(-3.18f, 0.61f, -6.09f, 1.84f, -9.31f, 2.75f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(31.04f, 46.42f)
                arcToRelative(6.26f, 8.95f, 0.0f, true, false, 12.52f, 0.0f)
                arcToRelative(6.26f, 8.95f, 0.0f, true, false, -12.52f, 0.0f)
                close()
            }
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(26.54f, 36.31f)
                curveToRelative(3.25f, -7.49f, 13.7f, -5.14f, 17.0f, 1.0f)
                arcTo(10.21f, 10.21f, 0.0f, false, true, 45.0f, 43.65f)
                arcToRelative(41.56f, 41.56f, 0.0f, false, true, -5.4f, -2.43f)
                curveToRelative(-4.37f, -2.34f, -7.7f, -4.52f, -13.08f, -4.91f)
                close()
            }
        }
            .build()
        return _icAngry!!
    }

private var _icAngry: ImageVector? = null
