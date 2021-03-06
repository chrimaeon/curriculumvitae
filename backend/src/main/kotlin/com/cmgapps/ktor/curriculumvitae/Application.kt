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

package com.cmgapps.ktor.curriculumvitae

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.Employment
import com.cmgapps.ktor.curriculumvitae.infra.di.appModule
import com.cmgapps.ktor.curriculumvitae.routes.registerEmploymentRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerHealthCheckRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerProfileRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerRootRouting
import com.cmgapps.ktor.curriculumvitae.routes.registerStaticRoutes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.application.log
import io.ktor.features.AutoHeadResponse
import io.ktor.features.CORS
import io.ktor.features.CachingHeaders
import io.ktor.features.CallLogging
import io.ktor.features.Compression
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.features.StatusPages
import io.ktor.features.deflate
import io.ktor.features.gzip
import io.ktor.features.origin
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.request.httpMethod
import io.ktor.request.httpVersion
import io.ktor.response.respond
import io.ktor.serialization.json
import io.ktor.websocket.WebSockets
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.Koin
import org.koin.ktor.ext.inject
import org.koin.logger.slf4jLogger
import org.slf4j.event.Level
import java.io.PrintWriter
import java.io.StringWriter
import java.time.Month
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.ExperimentalTime

enum class Routes(val route: String) {
    ROOT("/"),
    HEALTHZ("/healthz"),
    STATUS("/status"),
    PROFILE("/profile"),
    EMPLOYMENT("/employment")
}

fun Application.module() {
    installFeatures()
    initDb()
    registerRoutes()
}

@OptIn(ExperimentalTime::class)
fun Application.installFeatures() {
    install(DefaultHeaders)
    install(AutoHeadResponse)
    install(ContentNegotiation) {
        json()
    }
    install(Compression) {
        gzip()
        deflate()
    }
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val method = call.request.httpMethod
            val path = call.request.origin.uri
            val status = call.response.status()
            val httpVersion = call.request.httpVersion
            "$status: ${method.value} $path $httpVersion"
        }
    }
    install(StatusPages) {
        exception<Throwable> { cause ->
            StringWriter().use { stringWriter ->
                PrintWriter(stringWriter).use { printWriter ->
                    cause.printStackTrace(printWriter)
                }
                log.error(stringWriter.toString())
            }
            call.respond(HttpStatusCode.InternalServerError, "Internal Server Error")
            throw cause
        }
    }

    install(CachingHeaders) {
        options { outgoingContent ->
            val contentType = outgoingContent.contentType?.withoutParameters()
            when {
                contentType?.match(ContentType.Image.Any) == true -> CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = Duration.days(1).toInt(DurationUnit.SECONDS)
                    )
                )
                else -> null
            }
        }
    }

    install(CORS) {
        header(HttpHeaders.Authorization)
        header(HttpHeaders.ContentType)
        header(HttpHeaders.AccessControlAllowOrigin)
        // header("any header") if you want to add any header
        allowCredentials = true
        allowNonSimpleContentTypes = true
        anyHost()
    }

    install(WebSockets)

    install(Koin) {
        slf4jLogger()
        modules(appModule)
    }
}

fun Application.registerRoutes() {
    registerRootRouting()
    registerHealthCheckRoutes()
    registerStaticRoutes()
    registerProfileRoutes()
    registerEmploymentRoutes()
}

fun Application.initDb() {
    val database: CvDatabase by inject()
    database.employmentQueries.insertEmployment(
        Employment(
            id = 1,
            job_title = "Founder and Software Engineer",
            employer = "CMG Mobile Apps",
            start_date = LocalDate(2010, Month.JUNE, 1).toString(),
            end_date = null,
            city = "Graz",
            description = listOf("Line 1", "Line 2")
        )
    )
}
