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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.components.ContentLoading
import com.cmgapps.android.curriculumvitae.components.ShimmerLoading
import com.cmgapps.android.curriculumvitae.data.domain.Address
import com.cmgapps.android.curriculumvitae.data.domain.Profile
import com.cmgapps.android.curriculumvitae.infra.UiState
import com.cmgapps.android.curriculumvitae.infra.lifecycleAware
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.toPaddingValues
import timber.log.Timber

private const val TAG = "ProfileScreen"

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    viewModel: ProfileViewModel,
    onEmailClick: () -> Unit
) {
    val profileUiState by viewModel.profile.lifecycleAware()
        .collectAsState(initial = UiState.Init)

    when (profileUiState) {
        UiState.Loading -> ContentLoading()
        is UiState.Success -> Content(
            modifier = modifier,
            profile = (profileUiState as UiState.Success<Profile>).data,
            onEmailClick = onEmailClick,
            bottomContentPadding = bottomContentPadding,
        )
        is UiState.Error -> ContentError(
            error = (profileUiState as UiState.Error).error,
            screenName = "ProfileScreen"
        )
        else -> if (BuildConfig.DEBUG) {
            Timber.tag(TAG).d(profileUiState.javaClass.simpleName)
        }
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
                LocalWindowInsets.current.statusBars.toPaddingValues(additionalBottom = bottomContentPadding),
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
    val coilPainter = rememberCoilPainter(
        request = profile.profileImageUrl,
        fadeIn = true,
    )
    Box(
        modifier = modifier
            .size(imageSize)
            .clip(CircleShape)
    ) {
        Image(
            painter = coilPainter,
            modifier = Modifier.fillMaxSize(),
            contentDescription = null,
            contentScale = ContentScale.Crop
        )

        when (coilPainter.loadState) {
            ImageLoadState.Loading ->
                ShimmerLoading(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.onBackground
                )
            else -> {
                // Do nothing
            }
        }
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

    Text(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false, color = primaryColor),
            onClick = onEmailClick
        ),
        text = profile.email,
        style = MaterialTheme.typography.subtitle1.copy(color = primaryColor),
    )

    val context = LocalContext.current
    val phoneNumber = profile.phone

    Text(
        modifier = modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = rememberRipple(bounded = false, color = primaryColor),
            onClick = { context.onTelClick(phoneNumber) }
        ),
        text = phoneNumber,
        style = MaterialTheme.typography.subtitle1.copy(color = primaryColor),
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

// region Previews

private const val name = "Firstname Lastname"
private const val phone = "+43123456789"
private const val profileImageUrl = "http://"
private val address = Address("Street 1", "Graz", "8010")
private const val email = "me@home.at"

@Preview(name = "Content", widthDp = 320, heightDp = 640)
@Composable
fun PreviewContent() {
    Theme(darkTheme = false) {
        ProvideWindowInsets {
            Content(
                profile = Profile(
                    name = name,
                    phone = phone,
                    profileImageUrl = profileImageUrl,
                    intro = listOf("Line1", "Line2"),
                    address = address,
                    email = email,
                ),
                onEmailClick = {}
            )
        }
    }
}

@Preview(name = "Content Land", widthDp = 640, heightDp = 320)
@Composable
fun PreviewLandscapeContent() {
    Theme(darkTheme = false) {
        ProvideWindowInsets {
            Content(
                profile = Profile(
                    name = name,
                    phone = phone,
                    profileImageUrl = profileImageUrl,
                    intro = listOf("Line1", "Line2"),
                    address = address,
                    email = email,
                ),
                onEmailClick = {}
            )
        }
    }
}

@Preview(
    name = "Content Dark", widthDp = 320, heightDp = 640, showBackground = true,
    backgroundColor = 0xFF000000,
)
@Composable
fun PreviewDarkContent() {
    Theme(darkTheme = true) {
        ProvideWindowInsets {
            Content(
                profile = Profile(
                    name = name,
                    phone = phone,
                    profileImageUrl = profileImageUrl,
                    intro = listOf("Line1", "Line2"),
                    address = address,
                    email = email,
                ),
                onEmailClick = {}
            )
        }
    }
}
// endregion
