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

package com.cmgapps.android.curriculumvitae.ui.profile

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.transform.CircleCropTransformation
import com.cmgapps.android.compomaeon.ui.Theme
import com.cmgapps.android.curriculumvitae.R
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets

@Composable
fun ProfileScreen() {
    val insets = LocalWindowInsets.current
    Column(
        modifier = Modifier
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = with(LocalDensity.current) { insets.navigationBars.bottom.toDp() + 16.dp },
            )
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        Spacer(modifier = Modifier.height(with(LocalDensity.current) { insets.statusBars.top.toDp() + 16.dp }))
        Header()
        val profileText = buildString {
            append(stringResource(id = R.string.profile_1))
            append("\n\n")
            append(stringResource(id = R.string.profile_2))
        }
        Text(
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp),
            text = profileText
        )
    }
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
            text = stringResource(id = R.string.name),
            style = MaterialTheme.typography.h5
        )
    }
}

// region Preview
@Preview(name = "Light", widthDp = 320, heightDp = 480)
@Composable
fun PreviewDefaultProfileScreen() {
    Theme(darkTheme = false) {
        ProvideWindowInsets(consumeWindowInsets = true) {
            ProfileScreen()
        }
    }
}

@Preview(name = "Land", widthDp = 480, heightDp = 320)
@Composable
fun PreviewLandProfileScreen() {
    Theme(darkTheme = false) {
        ProvideWindowInsets(consumeWindowInsets = true) {
            ProfileScreen()
        }
    }
}

@Preview(name = "Dark", widthDp = 320, heightDp = 480)
@Composable
fun PreviewDarkProfileScreen() {
    Theme(darkTheme = true) {
        ProvideWindowInsets(consumeWindowInsets = true) {
            ProfileScreen()
        }
    }
}
// endregion
