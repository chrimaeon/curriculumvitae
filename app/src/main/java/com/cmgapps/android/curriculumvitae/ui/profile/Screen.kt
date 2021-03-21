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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.android.compomaeon.ui.Theme
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.data.Address
import com.cmgapps.android.curriculumvitae.data.Profile
import com.cmgapps.android.curriculumvitae.infra.Resource
import dev.chrisbanes.accompanist.coil.CoilImage
import dev.chrisbanes.accompanist.insets.LocalWindowInsets
import dev.chrisbanes.accompanist.insets.ProvideWindowInsets
import timber.log.Timber

@Composable
fun ProfileScreen(viewModel: ProfileViewModel, onEmailClick: () -> Unit) {
    val profileResource by viewModel.profile.observeAsState()

    when (profileResource) {
        is Resource.Loading -> ContentLoading()
        is Resource.Success -> Content(
            profile = (profileResource as Resource.Success<Profile>).data,
            onEmailClick = onEmailClick
        )
        is Resource.Error -> {
            Timber.tag("ProfileScreen")
                .e((profileResource as Resource.Error).error)
            ContentError()
        }
    }
}

@Composable
fun ContentLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ContentError() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier.background(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colors.error
            )
        ) {
            Text(
                modifier = Modifier.padding(16.dp),
                text = stringResource(id = R.string.generic_error),
                color = MaterialTheme.colors.onError
            )
        }
    }
}

@Composable
fun Content(profile: Profile, onEmailClick: () -> Unit) {
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
        Header(profile, onEmailClick)
        Text(
            style = MaterialTheme.typography.body1,
            modifier = Modifier.padding(top = 16.dp),
            text = profile.intro.joinToString("\n\n")
        )
    }
}

@Composable
fun Header(
    profile: Profile,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .clip(CircleShape)
        ) {
            val imageSize = maxWidth / 3
            CoilImage(
                modifier = Modifier
                    .width(imageSize)
                    .height(imageSize),
                data = "https://cv.cmgapps.com/assets/profile.jpg",
                contentDescription = null,
                loading = {
                    Box(Modifier.matchParentSize()) {
                        CircularProgressIndicator(Modifier.align(Alignment.Center))
                    }
                }
            )
        }
        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = stringResource(id = R.string.name),
            style = MaterialTheme.typography.h5
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = profile.address.street,
            style = MaterialTheme.typography.subtitle1
        )

        Text(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = "${profile.address.postalCode} ${profile.address.city}",
            style = MaterialTheme.typography.subtitle1
        )

        ClickableText(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            text = AnnotatedString(profile.email),
            style = MaterialTheme.typography.subtitle1,
            onClick = {
                onEmailClick()
            }
        )
    }
}

// region Previews
@Preview(name = "Error")
@Composable
fun PreviewContentError() {
    Theme(darkTheme = false) {
        ContentError()
    }
}

@Preview(name = "Error")
@Composable
fun PreviewDarkContentError() {
    Theme(darkTheme = true) {
        ContentError()
    }
}

@Preview(name = "Content", widthDp = 320, heightDp = 640)
@Composable
fun PreviewContent() {
    Theme(darkTheme = false) {
        ProvideWindowInsets {
            Content(
                profile = Profile(
                    lang = "de",
                    intro = listOf("Line1", "Line2"),
                    address = Address("Street 1", "Graz", "8010"),
                    email = "me@home.at"
                ),
                onEmailClick = {}
            )
        }
    }
}

@Preview(name = "Content", widthDp = 320, heightDp = 640)
@Composable
fun PreviewDarkContent() {
    Theme(darkTheme = true) {
        ProvideWindowInsets {
            Content(
                profile = Profile(
                    lang = "de",
                    intro = listOf("Line1", "Line2"),
                    address = Address("Street 1", "Graz", "8010"),
                    email = "me@home.at"
                ),
                onEmailClick = {}
            )
        }
    }
}
// endregion
