/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.routes

import io.ktor.server.application.Application
import io.ktor.server.http.content.resource
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.routing.routing

fun Application.registerStaticRoutes() {
    routing {
        static("assets") {
            resources("assets")
        }
        resource("favicon.ico", resourcePackage = "assets")
    }
}
