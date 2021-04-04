import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.p
import kotlinx.html.title
import java.lang.Long.signum
import java.text.StringCharacterIterator
import kotlin.math.abs

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

fun Route.healthCheck() {
    get("/healthz") {
        call.respond(HttpStatusCode.OK, "Ok")
    }

    get("/status") {
        call.respondHtml {
            head {
                title { +"Ktor Server" }
            }
            body {
                val runtime = Runtime.getRuntime()
                p { +"Available processors: ${runtime.availableProcessors()}" }
                p { +"Free memory: ${runtime.freeMemory().humanReadableSize()}" }
                p { +"Total memory: ${runtime.totalMemory().humanReadableSize()}" }
                p { +"Max. memory: ${runtime.maxMemory().humanReadableSize()}" }
            }
        }
    }
}

fun Long.humanReadableSize(): String {
    val absB = if (this == Long.MIN_VALUE) Long.MAX_VALUE else abs(this)
    if (absB < 1024) {
        return "$this B"
    }
    var value = absB
    val ci = StringCharacterIterator("KMGTPE")
    var i = 40
    while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
        value = value shr 10
        ci.next()
        i -= 10
    }
    value *= signum(this).toLong()
    return String.format("%.1f %ciB", value / 1024.0, ci.current())
}

fun Application.registerHealthCheckRoutes() {
    routing {
        healthCheck()
    }
}
