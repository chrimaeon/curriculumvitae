/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage

@Composable
fun BadSignature() {
    Scaffold(
        modifier = Modifier.systemBarsPadding(),
        backgroundColor = Color.DarkGray,
    ) { contentPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Box(
                    Modifier
                        .background(MaterialTheme.colors.error)
                        .fillMaxWidth(.8f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        modifier = Modifier.size(100.dp),
                        painter = painterResource(id = R.drawable.ic_angry),
                        contentDescription = DecorativeImage,
                        colorFilter = ColorFilter.tint(
                            MaterialTheme.colors.contentColorFor(
                                MaterialTheme.colors.error,
                            ),
                        ),
                    )
                }
                Box(
                    Modifier
                        .background(MaterialTheme.colors.surface)
                        .fillMaxWidth(.8f)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        stringResource(id = R.string.bad_signature),
                        color = MaterialTheme.colors.contentColorFor(MaterialTheme.colors.surface),
                        style = MaterialTheme.typography.h5,
                        fontFamily = FontFamily.Default,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@Composable
@Preview()
fun BadSignaturePreview() {
    Theme {
        BadSignature()
    }
}
