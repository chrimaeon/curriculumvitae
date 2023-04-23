package com.cmgapps.common.curriculumvitae.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CvIcons.HeartFill: ImageVector
    get() {
        if (_heartFill != null) {
            return _heartFill!!
        }
        _heartFill = Builder(
            name = "HeartFill",
            defaultWidth = 16.0.dp,
            defaultHeight =
            16.0.dp,
            viewportWidth = 16.0f,
            viewportHeight = 16.0f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(8.0f, 1.314f)
                curveTo(12.438f, -3.248f, 23.534f, 4.735f, 8.0f, 15.0f)
                curveTo(-7.534f, 4.736f, 3.562f, -3.248f, 8.0f, 1.314f)
                close()
            }
        }
            .build()
        return _heartFill!!
    }

private var _heartFill: ImageVector? = null
