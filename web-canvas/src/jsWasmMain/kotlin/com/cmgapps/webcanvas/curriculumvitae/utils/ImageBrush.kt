/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.webcanvas.curriculumvitae.utils

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush

class ImageBrush(private val image: ImageBitmap?) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        if (image == null) {
            return Shader.makeEmpty()
        }
        return ImageShader(image)
    }
}
