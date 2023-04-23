package com.cmgapps.common.curriculumvitae.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CvIcons.CodeSlash: ImageVector
    get() {
        if (_codeSlash != null) {
            return _codeSlash!!
        }
        _codeSlash = Builder(
            name = "CodeSlash",
            defaultWidth = 16.0.dp,
            defaultHeight =
            16.0.dp,
            viewportWidth = 16.0f,
            viewportHeight = 16.0f,
        ).apply {
            path(fill = SolidColor(Color(0xFF000000))) {
                moveTo(10.478f, 1.647f)
                arcToRelative(0.5f, 0.5f, 0.0f, true, false, -0.956f, -0.294f)
                lineToRelative(-4.0f, 13.0f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, false, 0.956f, 0.294f)
                lineToRelative(4.0f, -13.0f)
                close()
                moveTo(4.854f, 4.146f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, 0.0f, 0.708f)
                lineTo(1.707f, 8.0f)
                lineToRelative(3.147f, 3.146f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, -0.708f, 0.708f)
                lineToRelative(-3.5f, -3.5f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, 0.0f, -0.708f)
                lineToRelative(3.5f, -3.5f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, true, 0.708f, 0.0f)
                close()
                moveTo(11.146f, 4.146f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, false, 0.0f, 0.708f)
                lineTo(14.293f, 8.0f)
                lineToRelative(-3.147f, 3.146f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, false, 0.708f, 0.708f)
                lineToRelative(3.5f, -3.5f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, false, 0.0f, -0.708f)
                lineToRelative(-3.5f, -3.5f)
                arcToRelative(0.5f, 0.5f, 0.0f, false, false, -0.708f, 0.0f)
                close()
            }
        }
            .build()
        return _codeSlash!!
    }

private var _codeSlash: ImageVector? = null
