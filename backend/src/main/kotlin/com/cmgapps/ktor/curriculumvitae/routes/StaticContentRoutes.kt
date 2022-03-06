/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.routes

import io.ktor.application.Application
import io.ktor.http.content.resource
import io.ktor.http.content.resources
import io.ktor.http.content.static
import io.ktor.routing.routing

fun Application.registerStaticRoutes() {
    routing {
        static("assets") {
            resources("assets")
        }
        resource("favicon.ico", resourcePackage = "assets")
    }
}
