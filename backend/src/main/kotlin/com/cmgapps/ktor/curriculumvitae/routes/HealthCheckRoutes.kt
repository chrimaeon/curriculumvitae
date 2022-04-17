/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.network.Status
import com.cmgapps.ktor.curriculumvitae.Routes
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.Frame
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.healthCheck() {
    get(Routes.HEALTHZ.route) {
        call.respond(HttpStatusCode.OK, "Ok")
    }

    val runtime = Runtime.getRuntime()

    @OptIn(ExperimentalCoroutinesApi::class)
    webSocket(Routes.STATUS.route) {
        while (!incoming.isClosedForReceive) {
            outgoing.send(
                Frame.Text(
                    Json.encodeToString(
                        Status(
                            availableProcessors = runtime.availableProcessors(),
                            freeMemory = runtime.freeMemory(),
                            totalMemory = runtime.totalMemory(),
                            maxMemory = runtime.maxMemory(),
                        ),
                    ),
                ),
            )
            delay(2000L)
        }
    }
}

fun Application.registerHealthCheckRoutes() {
    routing {
        healthCheck()
    }
}
