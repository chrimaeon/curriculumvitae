package com.cmgapps.desktop.curriculumvitae.ui.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.ImageVector.Builder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val CvIcons.Logo: ImageVector
    get() {
        if (_icIos != null) {
            return _icIos!!
        }
        _icIos = Builder(
            name = "CvIcons.Logo",
            defaultWidth = 200.0.dp,
            defaultHeight = 200.0.dp,
            viewportWidth = 200.0f,
            viewportHeight = 200.0f,
        ).apply {
            path(fill = SolidColor(Color(0xFFFFFFFF))) {
                moveTo(143.8f, 150.8f)
                horizontalLineTo(56.2f)
                verticalLineToRelative(-10.2f)
                curveTo(56.2f, 126.0f, 85.4f, 118.0f, 100.0f, 118.0f)
                reflectiveCurveToRelative(43.8f, 8.0f, 43.8f, 22.6f)
                moveTo(100.0f, 63.3f)
                curveToRelative(12.1f, 0.0f, 21.9f, 9.8f, 21.9f, 21.9f)
                verticalLineToRelative(0.0f)
                curveToRelative(0.0f, 12.1f, -9.8f, 21.9f, -21.9f, 21.9f)
                reflectiveCurveToRelative(-21.9f, -9.8f, -21.9f, -21.9f)
                reflectiveCurveTo(87.9f, 63.3f, 100.0f, 63.3f)
                moveTo(100.0f, 34.2f)
                curveToRelative(4.0f, 0.0f, 7.3f, 3.3f, 7.3f, 7.3f)
                reflectiveCurveToRelative(-3.3f, 7.3f, -7.3f, 7.3f)
                reflectiveCurveToRelative(-7.3f, -3.3f, -7.3f, -7.3f)
                reflectiveCurveTo(96.0f, 34.2f, 100.0f, 34.2f)
                moveTo(151.0f, 34.2f)
                horizontalLineToRelative(-30.5f)
                curveToRelative(-3.1f, -8.5f, -11.1f, -14.6f, -20.6f, -14.6f)
                reflectiveCurveToRelative(-17.5f, 6.1f, -20.6f, 14.6f)
                horizontalLineTo(49.0f)
                curveToRelative(-8.1f, 0.0f, -14.6f, 6.5f, -14.6f, 14.6f)
                lineToRelative(0.0f, 0.0f)
                verticalLineToRelative(102.1f)
                curveToRelative(0.0f, 8.1f, 6.5f, 14.6f, 14.6f, 14.6f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                horizontalLineTo(151.0f)
                curveToRelative(8.1f, 0.0f, 14.6f, -6.5f, 14.6f, -14.6f)
                curveToRelative(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                verticalLineTo(48.7f)
                curveTo(165.6f, 40.7f, 159.1f, 34.2f, 151.0f, 34.2f)
                lineTo(151.0f, 34.2f)
                lineTo(151.0f, 34.2f)
                close()
            }
        }
            .build()
        return _icIos!!
    }

private var _icIos: ImageVector? = null
