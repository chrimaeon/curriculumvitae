/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.cmgapps.common.curriculumvitae.data.network.asDatabaseModel
import com.cmgapps.ktor.curriculumvitae.infra.di.Koin
import com.cmgapps.ktor.curriculumvitae.infra.di.appModule
import com.cmgapps.ktor.curriculumvitae.infra.di.inject
import com.cmgapps.ktor.curriculumvitae.routes.registerEmploymentRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerHealthCheckRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerProfileRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerRootRouting
import com.cmgapps.ktor.curriculumvitae.routes.registerSkillRoutes
import com.cmgapps.ktor.curriculumvitae.routes.registerStaticRoutes
import io.ktor.content.TextContent
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.CachingOptions
import io.ktor.http.withCharset
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import io.ktor.server.application.log
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.callloging.CallLogging
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.compression.deflate
import io.ktor.server.plugins.compression.gzip
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.origin
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.httpMethod
import io.ktor.server.request.httpVersion
import io.ktor.server.response.respond
import io.ktor.server.websocket.WebSockets
import kotlinx.serialization.serializer
import org.slf4j.event.Level
import java.io.PrintWriter
import java.io.StringWriter
import kotlin.time.Duration.Companion.days

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

enum class Routes(val route: String) {
    ROOT("/"),
    HEALTHZ("/healthz"),
    STATUS("/status"),
    PROFILE("/profile"),
    EMPLOYMENT("/employment"),
    SKILLS("/skills"),
}

fun Application.module() {
    installFeatures()
    initDb()
    registerRoutes()
}

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
        suspend fun ApplicationCall.respondCode(status: HttpStatusCode) {
            this.respond(
                TextContent(
                    "${status.value} ${status.description}",
                    ContentType.Text.Plain.withCharset(Charsets.UTF_8),
                    status,
                ),
            )
        }

        status(HttpStatusCode.NotFound) { call, statusCode ->
            call.respondCode(statusCode)
        }

        exception<Throwable> { call, cause ->
            StringWriter().use { stringWriter ->
                PrintWriter(stringWriter).use { printWriter ->
                    cause.printStackTrace(printWriter)
                }
                call.application.log.error(stringWriter.toString())
            }
            call.respond(HttpStatusCode.InternalServerError)
        }
    }

    install(CachingHeaders) {
        options { _, outgoingContent ->
            val contentType = outgoingContent.contentType?.withoutParameters()
            when {
                contentType?.match(ContentType.Image.Any) == true -> CachingOptions(
                    CacheControl.MaxAge(
                        maxAgeSeconds = 1.days.inWholeSeconds.toInt(),
                    ),
                )
                else -> null
            }
        }
    }

    install(CORS) {
        allowHeader(HttpHeaders.AccessControlAllowOrigin)
        anyHost()
    }

    install(WebSockets)

    install(Koin) {
        // Workaround for https://github.com/InsertKoinIO/koin/issues/1188
        logLevel = org.koin.core.logger.Level.ERROR
        modules(appModule)
    }
}

fun Application.registerRoutes() {
    registerRootRouting()
    registerHealthCheckRoutes()
    registerStaticRoutes()
    registerProfileRoutes()
    registerEmploymentRoutes()
    registerSkillRoutes()
}

fun Application.initDb() {
    val database: CvDatabase by inject()
    val modelLoader: ModelLoader by inject()

    database.transaction {
        modelLoader.loadModel<List<Employment>>(serializer(), "employments.json")
            ?.map(Employment::asDatabaseModel)
            ?.forEach {
                database.employmentQueries.insertEmployment(it)
            }
    }
}
