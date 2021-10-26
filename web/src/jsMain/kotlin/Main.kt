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

import co.touchlab.kermit.LogWriter
import co.touchlab.kermit.Logger
import co.touchlab.kermit.Severity
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.web.curriculumvitae.App
import io.ktor.utils.io.errors.IOException
import kotlinx.browser.window
import org.jetbrains.compose.web.renderComposable
import org.w3c.workers.RegistrationOptions

val koin = initKoin().koin

class ConsoleWriter : LogWriter() {
    override fun log(severity: Severity, message: String, tag: String, throwable: Throwable?) {
        var output = "[$tag] $message"
        throwable?.let {
            output += " ${it.stackTraceToString()}"
        }
        when (severity) {
            Severity.Assert, Severity.Error -> console.error(output)
            Severity.Warn -> console.warn(output)
            Severity.Info -> console.info(output)
            Severity.Debug, Severity.Verbose -> console.log(output)
        }
    }
}

fun main() {
    Logger.setLogWriters(ConsoleWriter())
    val logger = Logger.withTag("main")
    if (jsTypeOf(window.navigator.serviceWorker) != "undefined") {
        window.navigator.serviceWorker.register("/sw.js", RegistrationOptions(scope = "/")).then(
            onFulfilled = { reg ->
                when {
                    reg.installing != null -> logger.i("Service worker installing")
                    reg.waiting != null -> logger.i("Service worker installed")
                    reg.active != null -> logger.i("Service worker active")
                }
            },
            onRejected = {
                logger.w("Registration failed with $it")
            }
        )
    } else {
        logger.w("Service worker API not available")
    }

    logger.e(IOException("forced")) { "error in class" }

    renderComposable(rootElementId = "root") {
        App(koin)
    }
}
