/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.paparazzi

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import app.cash.paparazzi.DeviceConfig
import app.cash.paparazzi.Paparazzi
import com.android.resources.ScreenOrientation
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileViewModel
import com.cmgapps.common.curriculumvitae.infra.UiState
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

@RunWith(TestParameterInjector::class)
class ProfileScreenShould(
    @TestParameter config: Config,
    @TestParameter val themeColors: ThemeColors
) {

    @Suppress("unused")
    enum class Config(
        val deviceConfig: DeviceConfig
    ) {
        PIXEL_5(DeviceConfig.PIXEL_5),
        PIXEL_5_LAND(
            DeviceConfig.PIXEL_5.copy(
                screenWidth = DeviceConfig.PIXEL_5.screenHeight,
                screenHeight = DeviceConfig.PIXEL_5.screenWidth,
                orientation = ScreenOrientation.LANDSCAPE
            )
        )
    }

    @Suppress("unused")
    enum class ThemeColors(
        val colors: Colors
    ) {
        LIGHT_COLORS(lightColors()),
        DARK_COLORS(darkColors())
    }

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = config.deviceConfig,
        appCompatEnabled = false,
    )

    @Mock
    lateinit var viewModel: ProfileViewModel

    private var mockitoAnnotations: AutoCloseable? = null

    @Before
    fun beforeEach() {
        mockitoAnnotations = MockitoAnnotations.openMocks(this)
        whenever(viewModel.uiState) doReturn UiState(data = StubDomainProfile())
    }

    @After
    fun afterEach() {
        mockitoAnnotations?.close()
    }

    @Test
    fun renderOnPixel5() {
        paparazzi.snapshot {
            MaterialTheme(
                colors = themeColors.colors
            ) {
                Surface {
                    ProfileScreen(
                        viewModel = viewModel,
                        onEmailClick = {},
                        snackbarHostState = SnackbarHostState()
                    )
                }
            }
        }
    }
}
