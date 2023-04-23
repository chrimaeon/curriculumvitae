/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.awaitApplication
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.infra.di.DebugBaseUrlKey
import com.cmgapps.common.curriculumvitae.infra.di.getEmploymentRepository
import com.cmgapps.common.curriculumvitae.infra.di.getOssProjectRepository
import com.cmgapps.common.curriculumvitae.infra.di.getProfileRepository
import com.cmgapps.common.curriculumvitae.infra.di.getSkillRepository
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.common.curriculumvitae.ui.icon.CheckSquare
import com.cmgapps.common.curriculumvitae.ui.icon.CvIcons
import com.cmgapps.common.curriculumvitae.ui.icon.Logo
import com.cmgapps.common.curriculumvitae.ui.icon.Square
import com.cmgapps.desktop.curriculumvitae.ui.Theme
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.prefs.Preferences
import javax.imageio.ImageIO

private const val AppName = "Curriculum Vitae"

data class AppState(
    val profile: ProfileState? = null,
    val skills: List<Skill>? = null,
    val projects: List<OssProject>? = null,
    val employments: List<Employment>? = null,
    val backgroundImage: ImageBitmap? = null,
) {
    val loading: Boolean
        get() = profile == null ||
            skills == null ||
            projects == null ||
            employments == null ||
            backgroundImage == null
}

data class ProfileState(
    val profile: Profile,
    val profileImage: ImageBitmap,
)

val koin = initKoin(enableNetworkLogging = true).koin

fun main() = runBlocking {
    awaitApplication {
        var appState by remember { mutableStateOf(AppState()) }

        LaunchedEffect(Unit) {
            val skills = async {
                try {
                    koin.getSkillRepository().getSkills()
                } catch (exc: IOException) {
                    emptyList()
                }
            }

            val projects = async {
                try {
                    koin.getOssProjectRepository().getOssProjects()
                        .filter { !it.fork && !it.archived && !it.private }
                } catch (exc: IOException) {
                    emptyList()
                }
            }

            val profileRepo = koin.getProfileRepository()

            @Suppress("kotlin:S6310")
            val profileState = async {
                val profile =
                    profileRepo.getProfile()
                val profileImage =
                    withContext(Dispatchers.IO) {
                        ImageIO.read(
                            profileRepo.getProfileImage(profile.profileImagePath).toInputStream(),
                        ).toComposeImageBitmap()
                    }
                ProfileState(profile, profileImage)
            }

            val backgroundImage = async {
                useResource("/page_background.jpg") {
                    ImageIO.read(it).toComposeImageBitmap()
                }
            }

            appState = appState.copy(
                skills = skills.await(),
                projects = projects.await(),
                profile = profileState.await(),
                backgroundImage = backgroundImage.await(),
            )

            koin.getEmploymentRepository().getEmployments().collect {
                appState = appState.copy(employments = it)
            }
        }

        if (appState.loading) {
            SplashScreen(onCloseRequest = ::exitApplication)
        } else {
            val profile = appState.profile!!.profile
            val profileImage = appState.profile!!.profileImage
            val employments = appState.employments!!
            val skills = appState.skills!!
            val projects = appState.projects!!
            val backgroundImage = appState.backgroundImage!!

            MainScreen(
                backgroundImage = backgroundImage,
                profile = profile,
                profileImage = profileImage,
                employments = employments,
                skills = skills,
                projects = projects,
                onCloseRequest = ::exitApplication,
            )
        }
    }
}

@Composable
fun SplashScreen(onCloseRequest: () -> Unit) {
    Window(
        undecorated = true,
        onCloseRequest = onCloseRequest,
        title = AppName,
        state = WindowState(
            placement = WindowPlacement.Floating,
            position = WindowPosition(
                Alignment.Center,
            ),
            width = 600.dp,
            height = 400.dp,
        ),
    ) {
        Theme {
            Column(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colors.primary),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                val contentColor = MaterialTheme.colors.onPrimary
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        CvIcons.Logo,
                        modifier = Modifier.size(200.dp),
                        contentDescription = AppName,
                        tint = contentColor,
                    )
                    Text(AppName, style = MaterialTheme.typography.h4)
                }
                CircularProgressIndicator(color = contentColor)
            }
        }
    }
}

@Composable
fun MainScreen(
    backgroundImage: ImageBitmap,
    profile: Profile,
    profileImage: ImageBitmap,
    employments: List<Employment>,
    skills: List<Skill>,
    projects: List<OssProject>,
    onCloseRequest: () -> Unit,
) {
    Window(
        onCloseRequest = onCloseRequest,
        title = AppName,
        state = WindowState(height = 800.dp, width = 1000.dp),
    ) {
        MenuBar {
            Menu("Debug", 'd') {
                Menu("Change Base URL") {
                    val prefs = Preferences.userRoot()
                    var currentUrl by remember {
                        mutableStateOf(
                            prefs.get(
                                DebugBaseUrlKey,
                                BaseUrl,
                            ),
                        )
                    }
                    prefs.addPreferenceChangeListener {
                        if (it.key == DebugBaseUrlKey) {
                            currentUrl = it.newValue
                        }
                    }

                    DebugBaseUrls.forEach { debugUrl ->
                        Item(
                            debugUrl,
                            icon = if (currentUrl == debugUrl) {
                                rememberVectorPainter(CvIcons.CheckSquare)
                            } else {
                                rememberVectorPainter(CvIcons.Square)
                            },
                        ) {
                            prefs.apply {
                                put(DebugBaseUrlKey, debugUrl)
                                flush()
                            }
                        }
                    }
                }
            }
        }
        Theme {
            App(
                backgroundImage = backgroundImage,
                profile = profile,
                profileImage = profileImage,
                employments = employments,
                skills = skills,
                projects = projects,
            )
        }
    }
}
