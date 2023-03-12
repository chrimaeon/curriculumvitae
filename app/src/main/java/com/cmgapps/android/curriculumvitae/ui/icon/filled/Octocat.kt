/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.ui.icon.filled

import androidx.compose.material.icons.materialIcon
import androidx.compose.material.icons.materialPath
import androidx.compose.ui.graphics.vector.ImageVector
import com.cmgapps.android.curriculumvitae.ui.icon.CvIcons

@Suppress("unused")
val CvIcons.Filled.Octocat: ImageVector
    get() {
        if (_icOctocatFull != null) {
            return _icOctocatFull!!
        }
        _icOctocatFull = materialIcon(
            name = "Filled.Octocat",
        ) {
            materialPath {
                moveTo(12.0f, 0.296f)
                curveToRelative(-6.627f, 0.0f, -12.0f, 5.372f, -12.0f, 12.0f)
                curveToRelative(0.0f, 5.302f, 3.438f, 9.8f, 8.206f, 11.387f)
                curveToRelative(0.6f, 0.111f, 0.82f, -0.26f, 0.82f, -0.577f)
                curveToRelative(0.0f, -0.286f, -0.011f, -1.231f, -0.016f, -2.234f)
                curveToRelative(-3.339f, 0.726f, -4.043f, -1.416f, -4.043f, -1.416f)
                curveToRelative(-0.546f, -1.387f, -1.332f, -1.756f, -1.332f, -1.756f)
                curveToRelative(-1.089f, -0.745f, 0.082f, -0.729f, 0.082f, -0.729f)
                curveToRelative(1.205f, 0.085f, 1.839f, 1.237f, 1.839f, 1.237f)
                curveToRelative(1.07f, 1.834f, 2.807f, 1.304f, 3.492f, 0.997f)
                curveToRelative(0.108f, -0.775f, 0.418f, -1.305f, 0.762f, -1.605f)
                curveToRelative(-2.666f, -0.303f, -5.467f, -1.332f, -5.467f, -5.93f)
                curveToRelative(0.0f, -1.31f, 0.469f, -2.381f, 1.237f, -3.221f)
                curveToRelative(-0.125f, -0.302f, -0.536f, -1.523f, 0.116f, -3.176f)
                curveToRelative(0.0f, 0.0f, 1.008f, -0.322f, 3.301f, 1.23f)
                curveToRelative(0.957f, -0.266f, 1.984f, -0.399f, 3.004f, -0.404f)
                curveToRelative(1.02f, 0.005f, 2.047f, 0.138f, 3.006f, 0.404f)
                curveToRelative(2.291f, -1.553f, 3.297f, -1.23f, 3.297f, -1.23f)
                curveToRelative(0.653f, 1.653f, 0.243f, 2.873f, 0.118f, 3.176f)
                curveToRelative(0.769f, 0.84f, 1.235f, 1.911f, 1.235f, 3.221f)
                curveToRelative(0.0f, 4.609f, -2.807f, 5.624f, -5.479f, 5.921f)
                curveToRelative(0.43f, 0.372f, 0.814f, 1.103f, 0.814f, 2.222f)
                curveToRelative(0.0f, 1.606f, -0.014f, 2.898f, -0.014f, 3.293f)
                curveToRelative(0.0f, 0.319f, 0.216f, 0.694f, 0.824f, 0.576f)
                curveToRelative(4.765f, -1.588f, 8.199f, -6.085f, 8.199f, -11.385f)
                curveToRelative(0.0f, -6.628f, -5.373f, -12.0f, -12.0f, -12.0f)
                close()
            }
        }
        return _icOctocatFull!!
    }

private var _icOctocatFull: ImageVector? = null
