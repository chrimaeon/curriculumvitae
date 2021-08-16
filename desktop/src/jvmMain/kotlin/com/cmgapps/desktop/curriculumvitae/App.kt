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

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
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
import androidx.compose.ui.graphics.asPainter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.desktop.curriculumvitae.ui.Footer
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.Koin
import java.awt.Desktop
import java.awt.image.BufferedImage
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

private val listMutex = Mutex()

@OptIn(ExperimentalFoundationApi::class, ExperimentalStdlibApi::class)
@Composable
fun App(koin: Koin) {

    var items by remember { mutableStateOf(emptyList<Any>()) }

    val profileRepo: ProfileRepository = koin.get()
    val employmentRepo: EmploymentRepository = koin.get()

    LaunchedEffect(true) {
        val profile = profileRepo.getProfile()
        items = buildList {
            listMutex.withLock {
                add(profile)
                addAll(items)
            }
        }
    }

    LaunchedEffect(true) {
        val employments = employmentRepo.getEmployments()
        items = buildList {
            listMutex.withLock {
                if (items.isNotEmpty()) {
                    add(0, items[0])
                }
                addAll(employments)
            }
        }
    }

    Scaffold(
        bottomBar = {
            Footer()
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
        ) {
            val state = rememberLazyListState()

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = state,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(20.dp)
            ) {
                items(
                    items = items,
                    key = {
                        when (it) {
                            is Profile -> it.hashCode()
                            is Employment -> it.id
                            else -> error("type not supported: ${it::class.java.simpleName}")
                        }
                    }
                ) { item ->
                    when (item) {
                        is Profile -> ProfileCard(item)
                        is Employment -> EmploymentCard(item)
                    }
                }
            }
            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state
                )
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileCard(profile: Profile) {
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
                    painter = it.asPainter(),
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
                }.pointerIcon(PointerIcon.Hand),
                color = Colors.Blue
            )
            Text(
                profile.phone,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI.create("tel:${profile.phone}"))
                    }
                }.pointerIcon(PointerIcon.Hand),
                color = Colors.Blue
            )
        }
    }
}

@Composable
private fun EmploymentCard(employemnt: Employment) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = employemnt.employer,
                style = MaterialTheme.typography.h5

            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = employemnt.workPeriod.asHumanReadableString(),
                style = MaterialTheme.typography.h6
            )
            Spacer(Modifier.height(5.dp))
            Text(
                text = employemnt.jobTitle,
                style = MaterialTheme.typography.h6
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
