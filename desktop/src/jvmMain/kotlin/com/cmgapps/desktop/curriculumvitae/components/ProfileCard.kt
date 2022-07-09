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
import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.parameter.parametersOf
import org.koin.java.KoinJavaComponent.get
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.URI
import javax.imageio.ImageIO

@Suppress("BlockingMethodInNonBlockingContext")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileCard(profileRepository: ProfileRepository) {
    var profile: Profile? by remember { mutableStateOf(null) }
    var bitmap: BufferedImage? by remember { mutableStateOf(null) }

    LaunchedEffect(profileRepository) {
        try {
            profile = profileRepository.getProfile()
        } catch (exc: IOException) {
            val logger: Logger = get(Logger::class.java) {
                parametersOf("ProfileCard")
            }
            logger.e("Error loading profile", exc)
        }
    }

    LaunchedEffect(profile) {
        profile?.let {
            withContext(Dispatchers.IO) {
                bitmap = ImageIO.read(
                    profileRepository.getProfileImage(it.profileImagePath).toInputStream(),
                )
            }
        }
    }

    profile?.let {
        Card(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                bitmap?.let {
                    Image(
                        painter = it.toPainter(),
                        modifier = Modifier.size(200.dp).clip(CircleShape),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                    )
                }
                Text(
                    it.name,
                    style = MaterialTheme.typography.h3,
                )
                Text(
                    it.address.street,
                    style = MaterialTheme.typography.h5,
                )
                Text(
                    "${it.address.postalCode} ${it.address.city}",
                    style = MaterialTheme.typography.h5,
                )
                Text(
                    it.email,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.clickable {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().mail(URI.create("mailto:${it.email}"))
                        }
                    }.pointerHoverIcon(PointerIconDefaults.Hand),
                    color = MaterialTheme.colors.primary,
                )
                Text(
                    it.phone,
                    style = MaterialTheme.typography.h5,
                    modifier = Modifier.clickable {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI.create("tel:${it.phone}"))
                        }
                    }.pointerHoverIcon(PointerIconDefaults.Hand),
                    color = MaterialTheme.colors.primary,
                )
            }
        }
    }
}
