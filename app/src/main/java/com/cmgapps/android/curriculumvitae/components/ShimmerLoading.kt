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

package com.cmgapps.android.curriculumvitae.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush.Companion.linearGradient
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity

@Composable
fun ShimmerLoading(
    modifier: Modifier = Modifier,
    durationMs: Int = 500,
    delayMs: Int = 600,
    color: Color = MaterialTheme.colors.onSurface
) {
    BoxWithConstraints(
        modifier = modifier
    ) {
        val (widthPx, heightPx) = with(LocalDensity.current) { maxWidth.toPx() to maxHeight.toPx() }
        val gradientWidth: Float = (0.9f * widthPx)

        val infiniteTransition = rememberInfiniteTransition()
        val xShimmer by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = widthPx + gradientWidth,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMs,
                    easing = LinearEasing,
                    delayMillis = delayMs
                ),
                repeatMode = RepeatMode.Restart
            )
        )
        val yShimmer by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = heightPx + gradientWidth,
            animationSpec = infiniteRepeatable(
                animation = tween(
                    durationMillis = durationMs,
                    easing = LinearEasing,
                    delayMillis = delayMs
                ),
                repeatMode = RepeatMode.Restart
            )
        )
        val colors = listOf(
            color.copy(alpha = 0.1f),
            color.copy(alpha = 0.2f),
            color.copy(alpha = 0.1f),
        )

        val brush = linearGradient(
            colors = colors,
            start = Offset(
                xShimmer - gradientWidth,
                yShimmer - gradientWidth
            ),
            end = Offset(xShimmer, yShimmer)
        )
        Canvas(
            Modifier
                .fillMaxSize()
        ) {
            drawRect(brush = brush)
        }
    }
}
