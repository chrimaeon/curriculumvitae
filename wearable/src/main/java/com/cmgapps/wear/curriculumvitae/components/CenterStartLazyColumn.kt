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

package com.cmgapps.wear.curriculumvitae.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyColumnDefaults
import androidx.wear.compose.material.ScalingLazyListScope

@Composable
fun CenterStartLazyColumn(
    itemHeight: Dp,
    content: ScalingLazyListScope.() -> Unit,
) {
    BoxWithConstraints {
        val verticalPadding = (maxHeight / 2) - (itemHeight / 2)

        ScalingLazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
            contentPadding = PaddingValues(vertical = verticalPadding, horizontal = 16.dp),
            scalingParams = ScalingLazyColumnDefaults.scalingParams(
                edgeScale = 0.2f,
                minTransitionArea = 0.5f
            ),
            content = content
        )
    }
}
