/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.routes

import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.application
import io.ktor.server.application.call
import io.ktor.server.http.content.staticResources
import io.ktor.server.response.respond
import io.ktor.server.response.respondOutputStream
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.registerStaticRoutes() {
    routing {
        staticResources("assets", "assets")
        get("favicon.ico") {
            val iconStream =
                application.environment.classLoader.getResourceAsStream("assets/favicon.ico")

            if (iconStream == null) {
                call.respond(HttpStatusCode.NotFound)
                return@get
            }

            call.respondOutputStream(
                status = HttpStatusCode.OK,
                contentType = ContentType.Image.XIcon,
            ) {
                iconStream.copyTo(this)
            }
        }
    }
}
