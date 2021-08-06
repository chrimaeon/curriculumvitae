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

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.annotation.ExperimentalCoilApi
import coil.compose.ImagePainter
import coil.compose.LocalImageLoader
import coil.compose.rememberImagePainter
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.data.domain.Address
import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.ui.themedRipple
import com.cmgapps.android.curriculumvitae.util.ThemedPreview
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import timber.log.Timber

@LogTag
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: ProfileViewModel,
    onEmailClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val uiState = viewModel.uiState

    if (uiState.loading && uiState.data == null) {
        ContentLoading()
    }

    if (uiState.networkError) {
        val errorMessage = stringResource(id = R.string.refresh_error)
        LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(errorMessage)
        }
    }

    if (uiState.exception != null && !uiState.networkError && uiState.data == null) {
        ContentError()
    }

    if (uiState.data != null) {
        Content(
            modifier = modifier,
            profile = uiState.data,
            onEmailClick = onEmailClick,
            bottomContentPadding = bottomContentPadding,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    profile: Profile,
    onEmailClick: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp
            )
            .verticalScroll(rememberScrollState())
    ) {
        Column(
            modifier = Modifier.padding(
                rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.statusBars,
                    additionalBottom = bottomContentPadding
                ),
            )
        ) {
            Header(profile, onEmailClick)
            Text(
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(top = 16.dp),
                text = profile.intro.joinToString("\n\n")
            )
        }
    }
}

@Composable
private fun Header(
    profile: Profile,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth()
    ) {
        val isLandscape = minWidth > 600.dp
        val imageSize = if (isLandscape) minWidth / 6 else minWidth / 3

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageSize = imageSize,
                    profile = profile
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp)
                ) {
                    ProfileDetails(
                        profile = profile,
                        onEmailClick = onEmailClick
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageSize = imageSize,
                    profile = profile
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDetails(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    profile = profile,
                    onEmailClick = onEmailClick
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(modifier: Modifier = Modifier, imageSize: Dp, profile: Profile) {
    val coilPainter = rememberImagePainter(
        data = profile.profileImageUrl,
        imageLoader = LocalImageLoader.current,
        builder = {
            crossfade(true)
            error(
                drawableResId = R.drawable.ic_outline_person_24
            )
        }
    )

    var showPlaceholder by remember { mutableStateOf(true) }

    @OptIn(ExperimentalCoilApi::class)
    LaunchedEffect(coilPainter) {
        snapshotFlow { coilPainter.state }
            .filter {
                when (it) {
                    is ImagePainter.State.Success -> true
                    is ImagePainter.State.Error -> {
                        Timber.tag(ComposableProfileScreen.LOG_TAG).e(it.throwable)
                        true
                    }
                    else -> false
                }
            }
            .collect { showPlaceholder = false }
    }

    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
            .placeholder(
                visible = showPlaceholder,
                shape = CircleShape,
                highlight = PlaceholderHighlight.shimmer()
            )
    ) {
        Image(
            painter = coilPainter,
            modifier = Modifier.fillMaxSize(),
            contentDescription = DecorativeImage,
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
private fun ProfileDetails(
    modifier: Modifier = Modifier,
    profile: Profile,
    onEmailClick: () -> Unit
) {
    Text(
        modifier = modifier,
        text = profile.name,
        style = MaterialTheme.typography.h5
    )
    Text(
        modifier = modifier,
        text = profile.address.street,
        style = MaterialTheme.typography.subtitle1
    )

    Text(
        modifier = modifier,
        text = "${profile.address.postalCode} ${profile.address.city}",
        style = MaterialTheme.typography.subtitle1
    )

    val primaryColor = MaterialTheme.colors.primary

    CompositionLocalProvider(LocalIndication provides themedRipple(bounded = false)) {

        Text(
            modifier = modifier.clickable(onClick = onEmailClick),
            text = profile.email,
            style = MaterialTheme.typography.subtitle1.copy(color = primaryColor),
        )

        val context = LocalContext.current
        val phoneNumber = profile.phone

        Text(
            modifier = modifier.clickable(onClick = { context.onTelClick(phoneNumber) }),
            text = phoneNumber,
            style = MaterialTheme.typography.subtitle1.copy(color = primaryColor),
        )
    }
}

private fun Context.onTelClick(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

// region Previews
private val profile = Profile(
    "Firstname Lastname",
    "+43123456789",
    "http://",
    Address("Street 1", "Graz", "8010"),
    email = "me@home.at",
    listOf("Line1", "Line2"),
)

@Preview(name = "Content", widthDp = 320, heightDp = 680)
@Composable
fun PreviewContent() {
    ThemedPreview {
        ProvideWindowInsets {
            Content(
                profile = profile,
                onEmailClick = {}
            )
        }
    }
}

@Preview(name = "Content Land", widthDp = 680, heightDp = 320)
@Composable
fun PreviewLandscapeContent() {
    ThemedPreview {
        ProvideWindowInsets {
            Content(
                profile = profile,
                onEmailClick = {}
            )
        }
    }
}

@Preview(name = "Content Dark", widthDp = 320, heightDp = 680)
@Composable
fun PreviewDarkContent() {
    ThemedPreview(darkTheme = true) {
        ProvideWindowInsets {
            Content(
                profile = profile,
                onEmailClick = {}
            )
        }
    }
}
// endregion
