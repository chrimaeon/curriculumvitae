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

package com.cmgapps.wear.curriculumvitae.ui.profile

import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.foundation.CurvedLayout
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.foundation.curvedRow
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.curvedText
import coil.compose.rememberImagePainter
import com.cmgapps.common.curriculumvitae.data.domain.Address
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.wear.curriculumvitae.infra.AssetPath
import com.cmgapps.wear.curriculumvitae.ui.Theme
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import org.koin.androidx.compose.getViewModel

private val BLACK = Color.Black.copy(alpha = 0.6f)

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = getViewModel(),
) {
    val state = viewModel.uiState

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    shape = CircleShape,
                ),
        )
    }

    state.data?.let { Profile(it) }
}

@Composable
private fun Profile(profile: Profile) {
    Box {
        val coilPainter = rememberImagePainter(
            data = AssetPath(profile.profileImagePath),
            builder = {
                crossfade(true)
                error(
                    drawable = ColorDrawable(MaterialTheme.colors.primary.toArgb()),
                )
            },
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            painter = coilPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color(0x20000000), BlendMode.Multiply),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val brush = Brush.radialGradient(
                0f to Color.Transparent,
                0.40f to Color.Transparent,
                1f to BLACK,
            )
            drawRect(brush)
        }
        val textStyle = MaterialTheme.typography.display3.copy(color = LocalContentColor.current)
        CurvedLayout(
            anchor = 270f,
        ) {
            curvedRow {
                curvedText(
                    profile.name,
                    style = CurvedTextStyle(style = textStyle),
                )
            }
        }
        CurvedLayout(
            anchor = 90f,
        ) {
            curvedRow {
                curvedText(
                    profile.email,
                    style = CurvedTextStyle(textStyle),
                    clockwise = false,
                )
            }
        }
    }
}

@Preview(
    widthDp = 195,
    heightDp = 195,
)
@Composable
fun ProfilePreview() {
    val profile = Profile(
        name = "Christian Grach",
        phone = "+123456789",
        profileImagePath = "",
        address = Address("Street 1", "Graz", "8010"),
        email = "me@home.at",
        intro = listOf("Line 1"),
    )
    Theme {
        Profile(profile)
    }
}
