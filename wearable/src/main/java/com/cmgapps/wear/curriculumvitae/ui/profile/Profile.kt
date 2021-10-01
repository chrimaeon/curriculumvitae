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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.BasicCurvedText
import androidx.wear.compose.foundation.CurvedRow
import androidx.wear.compose.foundation.CurvedTextStyle
import androidx.wear.compose.material.LocalContentColor
import androidx.wear.compose.material.MaterialTheme
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberImagePainter
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material.placeholder
import com.google.accompanist.placeholder.material.shimmer
import org.koin.androidx.compose.getViewModel

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = getViewModel()
) {
    val state = viewModel.uiState

    if (state.loading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .placeholder(
                    visible = true,
                    highlight = PlaceholderHighlight.shimmer(),
                    shape = CircleShape
                )
        )
    }

    state.data?.let { Profile(it) }
}

@OptIn(ExperimentalCoilApi::class)
@Composable
private fun Profile(profile: Profile) {
    Box {
        val coilPainter = rememberImagePainter(
            data = profile.profileImageUrl,
            builder = {
                crossfade(true)
                error(
                    drawable = ColorDrawable(MaterialTheme.colors.primary.value.toInt())
                )
            }
        )
        Image(
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape),
            painter = coilPainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            colorFilter = ColorFilter.tint(Color(0x20000000), BlendMode.Multiply)
        )
        CurvedRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            BasicCurvedText(
                profile.name,
                style = CurvedTextStyle(
                    MaterialTheme.typography.display3.copy(
                        color = LocalContentColor.current,
                    )
                ),
            )
        }

        CurvedRow(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            anchor = 90f,
        ) {
            BasicCurvedText(
                profile.email,
                style = CurvedTextStyle(MaterialTheme.typography.title3.copy(color = LocalContentColor.current)),
                clockwise = false
            )
        }
    }
}
