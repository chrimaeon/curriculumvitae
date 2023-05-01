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
import android.content.res.Configuration
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.infra.AssetPath
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.data.domain.Address
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.material3.shimmer
import timber.log.Timber

@LogTag
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: ProfileViewModel,
    onEmailClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
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

    uiState.data?.let {
        Content(
            modifier = modifier,
            profile = it,
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
    onEmailClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp,
                top = 16.dp,
            )
            .verticalScroll(rememberScrollState()),
    ) {
        Column(
            modifier = Modifier.padding(
                WindowInsets.statusBars.add(WindowInsets(bottom = bottomContentPadding))
                    .asPaddingValues(),
            ),
        ) {
            Header(profile, onEmailClick)
            Text(
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 16.dp),
                text = profile.intro.joinToString("\n\n"),
            )
        }
    }
}

@Composable
private fun Header(
    profile: Profile,
    onEmailClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(
        modifier = modifier.fillMaxWidth(),
    ) {
        val isLandscape = minWidth > 600.dp
        val imageSize = if (isLandscape) minWidth / 6 else minWidth / 3

        if (isLandscape) {
            Row(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(
                    modifier = Modifier.align(Alignment.CenterVertically),
                    imageSize = imageSize,
                    profile = profile,
                )
                Column(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(start = 16.dp),
                ) {
                    ProfileDetails(
                        profile = profile,
                        onEmailClick = onEmailClick,
                    )
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileImage(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    imageSize = imageSize,
                    profile = profile,
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProfileDetails(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    profile = profile,
                    onEmailClick = onEmailClick,
                )
            }
        }
    }
}

@Composable
private fun ProfileImage(modifier: Modifier = Modifier, imageSize: Dp, profile: Profile) {
    var showPlaceholder by remember { mutableStateOf(true) }

    val coilPainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(AssetPath(profile.profileImagePath))
            .crossfade(true)
            .error(
                drawableResId = R.drawable.ic_outline_person_24,
            )
            .build(),
        onState = { state ->
            showPlaceholder = when (state) {
                is AsyncImagePainter.State.Success -> false
                is AsyncImagePainter.State.Error -> {
                    Timber.tag(ComposableProfileScreen.LOG_TAG).e(state.result.throwable)
                    false
                }

                else -> true
            }
        },
    )

    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
            .placeholder(
                visible = showPlaceholder,
                shape = CircleShape,
                highlight = PlaceholderHighlight.shimmer(),
            ),
    ) {
        Image(
            painter = coilPainter,
            modifier = Modifier.fillMaxSize(),
            contentDescription = DecorativeImage,
            contentScale = ContentScale.Crop,
        )
    }
}

@Composable
private fun ProfileDetails(
    modifier: Modifier = Modifier,
    profile: Profile,
    onEmailClick: () -> Unit,
) {
    Text(
        modifier = modifier,
        text = profile.name,
        style = MaterialTheme.typography.headlineSmall,
    )
    Text(
        modifier = modifier,
        text = profile.address.street,
        style = MaterialTheme.typography.titleMedium,
    )

    Text(
        modifier = modifier,
        text = "${profile.address.postalCode} ${profile.address.city}",
        style = MaterialTheme.typography.titleMedium,
    )

    val primaryColor = MaterialTheme.colorScheme.primary

    Text(
        modifier = modifier.clickable(onClick = onEmailClick),
        text = profile.email,
        style = MaterialTheme.typography.titleMedium.copy(color = primaryColor),
    )

    val context = LocalContext.current
    val phoneNumber = profile.phone

    Text(
        modifier = modifier.clickable(onClick = { context.onTelClick(phoneNumber) }),
        text = phoneNumber,
        style = MaterialTheme.typography.titleMedium.copy(color = primaryColor),
    )
}

private fun Context.onTelClick(phoneNumber: String) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$phoneNumber")
    }
    if (intent.resolveActivity(packageManager) != null) {
        startActivity(intent)
    }
}

// region Preview
@Preview(
    name = "Content",
    widthDp = 320,
    heightDp = 680,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Preview(
    name = "Content Dark",
    widthDp = 320,
    heightDp = 680,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000,
)
@Preview(
    name = "Content Land",
    widthDp = 680,
    heightDp = 320,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
)
@Composable
fun PreviewContent() {
    val profile = Profile(
        "Firstname Lastname",
        "+43123456789",
        "/profile.jpg",
        Address("Street 1", "Graz", "8010"),
        email = "me@home.at",
        listOf("Line1", "Line2"),
    )
    Theme {
        Content(
            profile = profile,
            onEmailClick = {},
        )
    }
}
// endregion
