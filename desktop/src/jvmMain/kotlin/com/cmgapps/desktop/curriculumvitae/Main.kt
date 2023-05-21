/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.desktop.curriculumvitae

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.res.useResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.awaitApplication
import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.data.domain.Status
import com.cmgapps.common.curriculumvitae.format.humanReadableSize
import com.cmgapps.common.curriculumvitae.infra.di.CvDispatchers
import com.cmgapps.common.curriculumvitae.infra.di.DebugBaseUrlKey
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.OssProjectRepository
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.cmgapps.common.curriculumvitae.repository.StatusRepository
import com.cmgapps.common.curriculumvitae.ui.icon.CvIcons
import com.cmgapps.common.curriculumvitae.ui.icon.Logo
import com.cmgapps.desktop.curriculumvitae.ui.Theme
import io.ktor.serialization.ContentConvertException
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.koin.compose.rememberKoinInject
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import java.io.IOException
import java.util.prefs.PreferenceChangeListener
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

@Composable
private fun rememberAppState(): AppState {
    var appState by remember { mutableStateOf(AppState()) }
    val ioDispatcher = rememberKoinInject<CoroutineDispatcher>(named(CvDispatchers.IO))
    val skillsRepository = rememberKoinInject<SkillsRepository>()
    val projectsRepository = rememberKoinInject<OssProjectRepository>()
    val profileRepository = rememberKoinInject<ProfileRepository>()
    val employmentRepository = rememberKoinInject<EmploymentRepository>()

    LaunchedEffect(
        ioDispatcher,
        skillsRepository,
        profileRepository,
        projectsRepository,
        employmentRepository,
    ) {
        val skills = async {
            try {
                skillsRepository.getSkills()
            } catch (exc: IOException) {
                emptyList()
            } catch (exc: ContentConvertException) {
                emptyList()
            }
        }

        val projects = async {
            try {
                projectsRepository.getOssProjects()
                    .filter { !it.fork && !it.archived && !it.private }
            } catch (exc: IOException) {
                emptyList()
            } catch (exc: ContentConvertException) {
                emptyList()
            }
        }

        @Suppress("kotlin:S6310", "BlockingMethodInNonBlockingContext")
        val profileState = async {
            try {
                val profile = profileRepository.getProfile()
                val profileImage = withContext(ioDispatcher) {
                    ImageIO.read(
                        profileRepository.getProfileImage(profile.profileImagePath)
                            .toInputStream(),
                    ).toComposeImageBitmap()
                }
                ProfileState(profile, profileImage)
            } catch (exc: IOException) {
                null
            } catch (exc: ContentConvertException) {
                null
            }
        }

        val backgroundImage = async {
            useResource("/page_background.jpg", ::loadImageBitmap)
        }

        appState = appState.copy(
            skills = skills.await(),
            projects = projects.await(),
            profile = profileState.await(),
            backgroundImage = backgroundImage.await(),
        )

        employmentRepository.getEmployments().collect {
            appState = appState.copy(employments = it)
        }
    }

    return appState
}

fun main() = runBlocking {
    initKoin(enableNetworkLogging = true)
    awaitApplication {
        val appState = rememberAppState()

        if (appState.loading) {
            SplashScreen(onCloseRequest = ::exitApplication)
        } else {
            val profileState = appState.profile ?: throw IllegalStateException("profile == null")
            val employmentsState =
                appState.employments ?: throw IllegalStateException("employments == null")
            val skillsState = appState.skills ?: throw IllegalStateException("skills == null")
            val projectsState = appState.projects ?: throw IllegalStateException("projects == null")
            val backgroundImage =
                appState.backgroundImage ?: ImageBitmap(width = 0, height = 0, hasAlpha = false)

            MainScreen(
                backgroundImage = backgroundImage,
                profile = profileState.profile,
                profileImage = profileState.profileImage,
                employments = employmentsState,
                skills = skillsState,
                projects = projectsState,
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
        val (showBackendStatus, setShowBackendStatus) = remember { mutableStateOf(false) }
        MainMenu(
            showBackendStatus = showBackendStatus,
            onShowBackendStatusChange = setShowBackendStatus,
        )
        Theme {
            Box(modifier = Modifier.fillMaxSize()) {
                App(
                    backgroundImage = backgroundImage,
                    profile = profile,
                    profileImage = profileImage,
                    employments = employments,
                    skills = skills,
                    projects = projects,
                )
                if (showBackendStatus) {
                    BackendStatusOverlay()
                }
            }
        }
    }
}

@Composable
private fun FrameWindowScope.MainMenu(
    showBackendStatus: Boolean,
    onShowBackendStatusChange: (Boolean) -> Unit,
) {
    MenuBar {
        Menu("Debug", 'd') {
            Menu("Change Base URL") {
                val prefs = Preferences.userRoot()
                var currentUrl by remember(prefs) {
                    mutableStateOf(
                        @Suppress("kotlin:S6518")
                        prefs.get(
                            DebugBaseUrlKey,
                            BaseUrl,
                        ),
                    )
                }
                DisposableEffect(prefs) {
                    val changeListener =
                        PreferenceChangeListener { evt ->
                            if (evt.key == DebugBaseUrlKey) {
                                currentUrl = evt.newValue
                            }
                        }
                    prefs.addPreferenceChangeListener(changeListener)

                    onDispose {
                        prefs.removePreferenceChangeListener(changeListener)
                    }
                }

                DebugBaseUrls.split(",").forEach { debugUrl ->
                    CheckboxItem(
                        debugUrl,
                        checked = currentUrl == debugUrl,
                    ) {
                        prefs.apply {
                            put(DebugBaseUrlKey, debugUrl)
                            flush()
                        }
                    }
                }
            }
            Menu("Status") {
                val text = if (showBackendStatus) "Hide Backend Status" else "Show Backend Status"

                Item(text) {
                    onShowBackendStatusChange(!showBackendStatus)
                }
            }
        }
    }
}

@Composable
private fun BoxScope.BackendStatusOverlay() {
    val statusRepo = rememberKoinInject<StatusRepository>()
    val logger: Logger = rememberKoinInject { parametersOf("BackendStatusOverlay") }
    val status by produceState(Status(0, 0, 0, 0)) {
        statusRepo.getStatus().collect {
            logger.d(it.toString())
            value = it
        }
    }

    CompositionLocalProvider(
        LocalContentColor provides Color.White.copy(alpha = 0.8f),
        LocalTextStyle provides MaterialTheme.typography.h6,
    ) {
        Column(
            modifier = Modifier.wrapContentHeight()
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Transparent,
                        0.6f to Color.Black.copy(alpha = 0.8f),
                    ),
                )
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            val memory = buildString {
                append("Memory: ")
                append(status.freeMemory.humanReadableSize())
                append(" / ")
                append(status.totalMemory.humanReadableSize())
                append(" max. ")
                append(status.maxMemory.humanReadableSize())
            }
            Box(modifier = Modifier.height(10.dp))
            Text("Processors: ${status.availableProcessors}")
            Text(memory)
        }
    }
}
