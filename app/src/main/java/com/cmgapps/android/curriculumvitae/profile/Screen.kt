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

package com.cmgapps.android.curriculumvitae.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.cmgapps.android.compomaeon.ui.Theme
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun ProfileScreen() {
    val insets = LocalWindowInsets.current
    Header(modifier = Modifier.padding(top = with(LocalDensity.current) { insets.statusBars.top.toDp() + 16.dp }))
}

@Composable
fun Header(
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            val imageSize = maxWidth / 3
            CoilImage(
                modifier = Modifier
                    .width(imageSize)
                    .height(imageSize),
                data = "https://cv.cmgapps.com/assets/profile.jpg",
                contentDescription = "My content description",
                loading = {
                    Box(Modifier.matchParentSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                },
                requestBuilder = { transformations(CircleCropTransformation()) },
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "Christian Grach",
            style = MaterialTheme.typography.h5
        )
    }
}

@Preview
@Composable
fun PreviewProfileScreen() {
    Theme {
        ProvideWindowInsets(consumeWindowInsets = true) {
            ProfileScreen()
        }
    }
}
