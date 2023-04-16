/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.cmgapps.webcanvas.curriculumvitae.App
import com.cmgapps.webcanvas.curriculumvitae.Theme
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() = onWasmReady {
    CanvasBasedWindow(
        title = "Curriculum Vitae",
        canvasElementId = "ComposeCanvas",
    ) {
        Theme {
            App()
        }
    }
}
