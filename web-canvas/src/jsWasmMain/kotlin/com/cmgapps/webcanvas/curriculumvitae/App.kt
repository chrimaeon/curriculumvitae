/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.webcanvas.curriculumvitae

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.BuildYear
import com.cmgapps.common.curriculumvitae.components.ProfileCard
import com.cmgapps.common.curriculumvitae.ui.Footer
import com.cmgapps.webcanvas.curriculumvitae.model.Address
import com.cmgapps.webcanvas.curriculumvitae.model.Profile
import com.cmgapps.webcanvas.curriculumvitae.utils.ImageBrush
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.orEmpty
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.resource
import org.jetbrains.skia.Image
import org.khronos.webgl.Int8Array

data class ProfileState(val profile: Profile, val profileImage: ImageBitmap)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val backgroundImage = resource("page_background.jpg").rememberImageBitmap().orEmpty()

    val profileState by produceState<ProfileState?>(null) {
        val response = window.fetch("$BaseUrl/profile").await()
        val json = response.json().await().asDynamic()

        val imageResponse = window.fetch("$BaseUrl${json.profileImagePath}").await()
        val imageBytes = imageResponse.arrayBuffer().await()

        @Suppress("UnsafeCastFromDynamic")
        value = ProfileState(
            profile =
            Profile(
                name = json.name,
                phone = json.phone,
                profileImagePath = json.profileImagePath,
                email = json.email,
                intro = json.intro,
                address = Address(
                    street = json.address.street,
                    city = json.address.city,
                    postalCode = json.address.postalCode,
                ),
            ),
            profileImage = Image.makeFromEncoded(Int8Array(imageBytes).unsafeCast<ByteArray>())
                .toComposeImageBitmap(),
        )
    }

    Scaffold(
        bottomBar = {
            Footer(
                copyRightText = "Copyright © $BuildYear Christian Grach",
                onComposeDesktopClick = {
                    window.open(
                        "https://www.jetbrains.com/lp/compose-multiplatform/",
                        target = "_blank",
                    )
                },
                onGithubClick = {
                    window.open("https://github.com/chrimaeon/curriculumvitae", target = "_blank")
                },
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().background(
                ImageBrush(backgroundImage),
            ).padding(innerPadding),
        ) {
            val state = rememberScrollState()

            Column(
                modifier = Modifier.fillMaxSize()
                    .verticalScroll(state = state)
                    .padding(top = 250.dp, bottom = 20.dp, start = 20.dp, end = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                profileState?.let {
                    val profile = it.profile
                    ProfileCard(
                        name = profile.name,
                        street = profile.address.street,
                        city = profile.address.city,
                        postalCode = profile.address.city,
                        email = profile.email,
                        phone = profile.phone,
                        profileImage = it.profileImage,
                        onEmailClicked = {
                            window.open("mailto:${profile.email}", target = "_blank")
                        },
                        onPhoneClicked = {
                            window.open("tel:${profile.phone}", target = "_blank")
                        },
                    )
                }
            }
        }
    }
}
