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

import androidx.compose.foundation.LocalScrollbarStyle
import androidx.compose.foundation.ScrollbarStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.cmgapps.common.curriculumvitae.infra.di.DebugBaseUrlKey
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.desktop.curriculumvitae.App
import java.util.prefs.Preferences

private val koin = initKoin(enableNetworkLogging = true).koin

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Curriculum Vitae"
    ) {
        MenuBar {
            Menu("Debug", 'd') {
                Menu("Change Base URL") {
                    val prefs = Preferences.userRoot()
                    var currentUrl by remember {
                        mutableStateOf(
                            prefs.get(
                                DebugBaseUrlKey,
                                BaseUrl
                            )
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
                                painterResource("icons/check-square.svg")
                            } else {
                                painterResource("icons/square.svg")
                            }
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
        CompositionLocalProvider(
            LocalScrollbarStyle provides ScrollbarStyle(
                minimalHeight = 16.dp,
                thickness = 8.dp,
                shape = MaterialTheme.shapes.small,
                hoverDurationMillis = 300,
                unhoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.12f),
                hoverColor = MaterialTheme.colors.onSurface.copy(alpha = 0.50f)
            )
        ) {
            App(koin)
        }
    }
}
