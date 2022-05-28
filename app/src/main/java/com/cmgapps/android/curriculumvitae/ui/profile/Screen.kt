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
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.BiasAlignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
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
import com.cmgapps.android.curriculumvitae.infra.AssetPath
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.sensor.SensorData
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.android.curriculumvitae.ui.themedRipple
import com.cmgapps.common.curriculumvitae.data.domain.Address
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.ProvideWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
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

    val sensorData by viewModel.sensorData.collectAsState(initial = SensorData(.0, .0))

    uiState.data?.let {
        Content(
            modifier = modifier,
            profile = it,
            onEmailClick = onEmailClick,
            bottomContentPadding = bottomContentPadding,
            sensorData = sensorData,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier = Modifier,
    bottomContentPadding: Dp = 0.dp,
    profile: Profile,
    sensorData: SensorData,
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
            Header(
                profile = profile,
                onEmailClick = onEmailClick,
                sensorData = sensorData,
            )
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
    modifier: Modifier = Modifier,
    profile: Profile,
    sensorData: SensorData,
    onEmailClick: () -> Unit,
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
                    profile = profile,
                    sensorData = sensorData
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
                    profile = profile,
                    sensorData = sensorData,
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
private fun ProfileImage(
    modifier: Modifier = Modifier,
    imageSize: Dp,
    profile: Profile,
    sensorData: SensorData
) {

    Timber.d(sensorData.toString())
    val coilPainter = rememberImagePainter(
        data = AssetPath(profile.profileImagePath),
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
                        Timber.tag(ComposableProfileScreen.LOG_TAG).e(it.result.throwable)
                        true
                    }
                    else -> false
                }
            }
            .collect { showPlaceholder = false }
    }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        val pitch = sensorData.pitch + 90
        val roll = sensorData.roll
        if (!showPlaceholder) {
            Image(
                painter = coilPainter,
                modifier = Modifier
                    .offset(
                        x = (-roll * 0.5).dp,
                        y = (pitch * 0.7).dp
                    )
                    .size(imageSize - 48.dp)
                    .blur(radius = 24.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
                contentDescription = DecorativeImage,
                contentScale = ContentScale.Crop,
            )
        }

        Image(
            painter = coilPainter,
            modifier = Modifier
                .then(
                    if (!showPlaceholder) Modifier.offset(
                        x = (roll * 0.4).dp,
                        y = (-pitch * 0.4).dp
                    ) else Modifier
                )
                .size(imageSize)
                .placeholder(
                    visible = showPlaceholder,
                    shape = RoundedCornerShape(4.dp),
                    highlight = PlaceholderHighlight.shimmer()
                )
                .clip(RoundedCornerShape(4.dp)),
            contentDescription = DecorativeImage,
            contentScale = ContentScale.Crop,
            alignment = BiasAlignment(
                horizontalBias = -(roll * 0.001).toFloat(),
                verticalBias = 0f,
            )
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

// region Preview
@Preview(
    name = "Content",
    widthDp = 320,
    heightDp = 680,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Preview(
    name = "Content Dark",
    widthDp = 320,
    heightDp = 680,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF000000
)
@Preview(
    name = "Content Land",
    widthDp = 680,
    heightDp = 320,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
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
        ProvideWindowInsets {
            Content(
                profile = profile,
                sensorData = SensorData(.0, .0),
                onEmailClick = {}
            )
        }
    }
}
// endregion
