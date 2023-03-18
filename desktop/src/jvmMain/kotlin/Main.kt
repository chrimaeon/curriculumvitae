/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.singleWindowApplication
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.cmgapps.common.curriculumvitae.infra.di.DebugBaseUrlKey
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.desktop.curriculumvitae.App
import com.cmgapps.desktop.curriculumvitae.ui.Theme
import java.util.prefs.Preferences

private val koin = initKoin(enableNetworkLogging = true).koin

fun main() =
    singleWindowApplication(
        title = "Curriculum Vitae",
        state = WindowState(height = 800.dp, width = 800.dp),
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
                                painterResource("icons/check-square.svg")
                            } else {
                                painterResource("icons/square.svg")
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
            App(koin)
        }
    }
