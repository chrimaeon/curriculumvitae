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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.cmgapps.webcanvas.curriculumvitae.component.Address
import com.cmgapps.webcanvas.curriculumvitae.component.Profile
import com.cmgapps.webcanvas.curriculumvitae.component.ProfileCard
import com.cmgapps.webcanvas.curriculumvitae.utils.ImageBrush
import kotlinx.browser.window
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.orEmpty
import org.jetbrains.compose.resources.rememberImageBitmap
import org.jetbrains.compose.resources.resource

private val profile = Profile(
    name = "My Name",
    phone = "+10900020979",
    profileImagePath = "/profile.png",
    address = Address(
        street = "At Home 42",
        city = "City",
        postalCode = "90210",
    ),
    email = "me@domain.invalid",
    intro = listOf(
        "Some words I have to say about myself",
    ),
)

@OptIn(ExperimentalResourceApi::class)
@Composable
fun App() {
    val backgroundImage = resource("page_background.jpg").rememberImageBitmap().orEmpty()

    val profileImage = resource("profile.jpg").rememberImageBitmap().orEmpty()
    Scaffold(
        bottomBar = {
            Surface(
                color = MaterialTheme.colors.primarySurface,
            ) {
                Box(modifier = Modifier.fillMaxWidth().height(50.dp))
            }
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
                ProfileCard(
                    profile = profile,
                    profileImage = profileImage,
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
