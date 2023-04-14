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

package com.cmgapps.desktop.curriculumvitae.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.URI

@Composable
fun ProfileCard(
    profile: Profile,
    profileImage: BufferedImage,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = profileImage.toPainter(),
                modifier = Modifier.size(200.dp).clip(CircleShape),
                contentDescription = null,
                contentScale = ContentScale.Crop,
            )

            Text(
                profile.name,
                style = MaterialTheme.typography.h3,
            )
            Text(
                profile.address.street,
                style = MaterialTheme.typography.h5,
            )
            Text(
                "${profile.address.postalCode} ${profile.address.city}",
                style = MaterialTheme.typography.h5,
            )
            Text(
                profile.email,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().mail(URI.create("mailto:${profile.email}"))
                    }
                }.pointerHoverIcon(PointerIcon.Hand),
                color = MaterialTheme.colors.primary,
            )
            Text(
                profile.phone,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI.create("tel:${profile.phone}"))
                    }
                }.pointerHoverIcon(PointerIcon.Hand),
                color = MaterialTheme.colors.primary,
            )
        }
    }
}
