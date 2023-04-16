/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.webcanvas.curriculumvitae.component

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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp

/**
 * TODO placeholder until `:common` can be shared
 */
data class Profile(
    val name: String,
    val phone: String,
    val profileImagePath: String,
    val address: Address,
    val email: String,
    val intro: List<String>,
)

/**
 * TODO placeholder until `:common` can be shared
 */
data class Address(val street: String, val city: String, val postalCode: String)

@Composable
fun ProfileCard(
    profile: Profile,
    profileImage: ImageBitmap,
    onEmailClicked: () -> Unit,
    onPhoneClicked: () -> Unit,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                bitmap = profileImage,
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
                    onEmailClicked()
                }.pointerHoverIcon(PointerIcon.Hand),
                color = MaterialTheme.colors.primary,
            )
            Text(
                profile.phone,
                style = MaterialTheme.typography.h5,
                modifier = Modifier.clickable {
                    onPhoneClicked()
                }.pointerHoverIcon(PointerIcon.Hand),
                color = MaterialTheme.colors.primary,
            )
        }
    }
}
