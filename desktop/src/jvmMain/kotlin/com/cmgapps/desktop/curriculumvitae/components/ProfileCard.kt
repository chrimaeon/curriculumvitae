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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toPainter
import androidx.compose.ui.input.pointer.PointerIconDefaults
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.desktop.curriculumvitae.Colors
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileCard(profile: Profile?) {
    if (profile == null) {
        return
    }

    var bitmap: BufferedImage? by remember { mutableStateOf(null) }

    LaunchedEffect(profile.profileImageUrl) {
        bitmap = loadImage(profile.profileImageUrl)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            bitmap?.let {
                Image(
                    painter = it.toPainter(),
                    modifier = Modifier.size(200.dp).clip(CircleShape),
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                profile.name,
                style = MaterialTheme.typography.h3
            )
            Text(
                profile.address.street,
                style = MaterialTheme.typography.h5
            )
            Text(
                "${profile.address.postalCode} ${profile.address.city}",
                style = MaterialTheme.typography.h5
            )
            Text(
                profile.email,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().mail(URI.create("mailto:${profile.email}"))
                    }
                }.pointerHoverIcon(PointerIconDefaults.Hand),
                color = Colors.Blue
            )
            Text(
                profile.phone,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI.create("tel:${profile.phone}"))
                    }
                }.pointerHoverIcon(PointerIconDefaults.Hand),
                color = Colors.Blue
            )
        }
    }
}

private fun loadImage(source: String): BufferedImage? {
    try {
        val url = URL(source)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.connectTimeout = 5000
        connection.connect()

        return connection.inputStream.use {
            ImageIO.read(it)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return null
}