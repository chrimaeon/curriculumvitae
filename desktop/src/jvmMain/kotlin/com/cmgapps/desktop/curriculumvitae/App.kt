/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.desktop.curriculumvitae

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.OssProjectRepository
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.cmgapps.desktop.curriculumvitae.components.EmploymentCard
import com.cmgapps.desktop.curriculumvitae.components.OssProjectCard
import com.cmgapps.desktop.curriculumvitae.components.ProfileCard
import com.cmgapps.desktop.curriculumvitae.components.SkillsCard
import com.cmgapps.desktop.curriculumvitae.ui.Footer
import org.jetbrains.skia.Image
import org.jetbrains.skia.Shader
import org.jetbrains.skiko.toImage
import org.koin.core.Koin
import java.io.IOException
import javax.imageio.ImageIO

@Composable
fun App(koin: Koin) {
    val employmentRepo: EmploymentRepository = koin.get()
    val skillsRepo: SkillsRepository = koin.get()
    val ossProjectRepository: OssProjectRepository = koin.get()

    var skills: List<Skill>? by remember { mutableStateOf(null) }
    var projects: List<OssProject> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(skillsRepo) {
        skills = try {
            skillsRepo.getSkills()
        } catch (exc: IOException) {
            null
        }
    }

    LaunchedEffect(ossProjectRepository) {
        projects = try {
            ossProjectRepository.getOssProjects().filter { !it.fork && !it.archived && !it.private }
        } catch (exc: IOException) {
            emptyList()
        }
    }

    val employments by employmentRepo.getEmployments().collectAsState(emptyList())
    var backgroundImage: Image? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        backgroundImage = useResource("/page_background.jpg") {
            ImageIO.read(it).toImage()
        }
    }

    Scaffold(
        bottomBar = {
            Footer()
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
                ProfileCard(profileRepository = koin.get())
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

class ImageBrush(private val image: Image?) : ShaderBrush() {
    override fun createShader(size: Size): Shader {
        if (image == null) {
            return Shader.makeEmpty()
        }

        return ImageShader(image.toComposeImageBitmap())
    }
}
