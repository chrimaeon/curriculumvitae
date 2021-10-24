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

import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.web.curriculumvitae.App
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable
import org.w3c.workers.RegistrationOptions

val koin = initKoin().koin

fun main() {
    if (jsTypeOf(window.navigator.serviceWorker) != "undefined") {
        window.navigator.serviceWorker.register("/sw.js", RegistrationOptions(scope = "/")).then(
            onFulfilled = { reg ->
                when {
                    reg.installing != null -> console.log("Service worker installing")
                    reg.waiting != null -> console.log("Service worker installed")
                    reg.active != null -> console.log("Service worker active")
                }
            },
            onRejected = {
                console.log("Registration failed with $it")
            }
        )
    } else {
        console.warn("Service worker API not available")
    }

    renderComposable(rootElementId = "root") {
        App(koin)
    }
}
