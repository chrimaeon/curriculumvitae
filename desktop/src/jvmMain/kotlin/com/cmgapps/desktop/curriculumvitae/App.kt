/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.CopyRightText
import com.cmgapps.common.curriculumvitae.GitHubLink
import com.cmgapps.common.curriculumvitae.components.ProfileCard
import com.cmgapps.common.curriculumvitae.components.amber500
import com.cmgapps.common.curriculumvitae.components.lightBlue500
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.ui.Footer
import com.cmgapps.common.curriculumvitae.ui.Theme
import com.cmgapps.desktop.curriculumvitae.components.EmploymentCard
import com.cmgapps.desktop.curriculumvitae.components.OssProjectCard
import com.cmgapps.desktop.curriculumvitae.components.SkillsCard
import org.jetbrains.skia.Shader
import java.awt.Desktop
import java.net.URI

@Composable
fun App(
    profile: Profile,
    profileImage: ImageBitmap,
    employments: List<Employment>,
    skills: List<Skill>,
    projects: List<OssProject>,
    backgroundImage: ImageBitmap,
) {
    Scaffold(
        bottomBar = {
            Footer(
                copyRightText = CopyRightText,
                onComposeDesktopClick = {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop()
                            .browse(URI.create("https://github.com/jetbrains/compose-jb"))
                    }
                },
                onGithubClick = {
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().browse(URI.create(GitHubLink))
                    }
                },
            )
        },
    ) { innerPadding ->
        Box(
            Modifier.fillMaxSize().background(brush = ImageBrush(backgroundImage))
                .padding(innerPadding),
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
                    name = profile.name,
                    street = profile.address.street,
                    city = profile.address.city,
                    postalCode = profile.address.city,
                    email = profile.email,
                    phone = profile.phone,
                    profileImage = profileImage,
                    onEmailClicked = {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().mail(URI.create("mailto:${profile.email}"))
                        }
                    },
                    onPhoneClicked = {
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().browse(URI.create("tel:${profile.phone}"))
                        }
                    },
                )
                for (index in employments.indices step 2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        EmploymentCard(
                            modifier = Modifier.weight(1F),
                            employment = employments[index],
                        )
                        if (index + 1 <= employments.lastIndex) {
                            EmploymentCard(
                                modifier = Modifier.weight(1F),
                                employment = employments[index + 1],
                            )
                        } else {
                            Box(modifier = Modifier.weight(1F))
                        }
                    }
                }

                SkillsCard(skills)

                for (index in projects.indices step 2) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        OssProjectCard(
                            modifier = Modifier.weight(1F),
                            ossProject = projects[index],
                        )
                        if (index + 1 <= projects.lastIndex) {
                            OssProjectCard(
                                modifier = Modifier.weight(1F),
                                ossProject = projects[index + 1],
                            )
                        } else {
                            Box(modifier = Modifier.weight(1F))
                        }
                    }
                }
            }

            VerticalScrollbar(
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
                adapter = rememberScrollbarAdapter(
                    scrollState = state,
                ),
            )
        }
    }
}

class ImageBrush(private val image: ImageBitmap?) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        if (image == null) {
            return Shader.makeEmpty()
        }

        return ImageShader(image)
    }
}

@Preview()
@Composable
fun PreviewProfile() {
    val imageSize = 200
    val image = ImageBitmap(imageSize, imageSize)
    Canvas(image).apply {
        drawRect(
            left = 0F,
            top = 0F,
            right = imageSize.toFloat(),
            bottom = imageSize.toFloat(),
            paint = Paint().apply {
                color = lightBlue500
                isAntiAlias = true
            },
        )
        drawRect(
            left = imageSize.toFloat() / 4,
            top = imageSize.toFloat() / 4,
            right = imageSize.toFloat() / 4 * 3,
            bottom = imageSize.toFloat() / 4 * 3,
            paint = Paint().apply {
                color = amber500
                isAntiAlias = true
            },
        )
    }
    Theme {
        ProfileCard(
            name = "Firstname Lastname",
            street = "Home 1234",
            city = "City",
            postalCode = "90210",
            email = "me@invalid.email",
            phone = "+1234567890",
            profileImage = image,
            onEmailClicked = {},
            onPhoneClicked = {},
        )
    }
}
