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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.Placeable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.max

@Composable
fun TagCloud(
    modifier: Modifier = Modifier,
    verticalSpacing: Dp = 8.dp,
    horizontalSpacing: Dp = 8.dp,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content,
    ) { measurables, constraints ->
        val sequences = mutableListOf<List<Placeable>>()
        val horizontalSizes = mutableListOf<Int>()
        val verticalPositions = mutableListOf<Int>()

        var horizontalSpace = 0
        var verticalSpace = 0

        val currentSequence = mutableListOf<Placeable>()
        var currentHorizontalSize = 0
        var currentVerticalSize = 0

        fun canAddToCurrentSequence(placeable: Placeable) =
            currentSequence.isEmpty() || currentHorizontalSize + horizontalSpacing.roundToPx() +
                placeable.width <= constraints.maxWidth

        // Store current sequence information and start a new sequence.
        fun startNewSequence() {
            if (sequences.isNotEmpty()) {
                verticalSpace += verticalSpacing.roundToPx()
            }
            sequences += currentSequence.toList()
            horizontalSizes += currentVerticalSize
            verticalPositions += verticalSpace

            verticalSpace += currentVerticalSize
            horizontalSpace = max(horizontalSpace, currentHorizontalSize)

            currentSequence.clear()
            currentHorizontalSize = 0
            currentVerticalSize = 0
        }

        for (measurable in measurables) {
            val placeable = measurable.measure(constraints)

            if (!canAddToCurrentSequence(placeable)) startNewSequence()

            if (currentSequence.isNotEmpty()) {
                currentHorizontalSize += horizontalSpacing.roundToPx()
            }

            currentSequence.add(placeable)
            currentHorizontalSize += placeable.width
            currentVerticalSize = max(currentVerticalSize, placeable.height)
        }

        if (currentSequence.isNotEmpty()) startNewSequence()

        val layoutWidth = max(horizontalSpace, constraints.minWidth)
        val layoutHeight = max(verticalSpace, constraints.minHeight)

        layout(layoutWidth, layoutHeight) {
            sequences.forEachIndexed { i, placeables ->
                val childrenXSizes = IntArray(placeables.size) { j ->
                    placeables[j].width +
                        if (j < placeables.lastIndex) horizontalSpacing.roundToPx() else 0
                }

                val horizontalPositions = IntArray(childrenXSizes.size) { 0 }
                with(Arrangement.Center) {
                    arrange(layoutWidth, childrenXSizes, horizontalPositions)
                }

                placeables.forEachIndexed { j, placeable ->
                    placeable.place(
                        x = horizontalPositions[j],
                        y = verticalPositions[i]
                    )
                }
            }
        }
    }
}
