/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.network.Profile
import com.cmgapps.ktor.curriculumvitae.Language
import com.cmgapps.ktor.curriculumvitae.ModelLoader
import com.cmgapps.ktor.curriculumvitae.Routes
import com.cmgapps.ktor.curriculumvitae.infra.di.inject
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.acceptLanguageItems
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.serializer

private fun Route.profileRouting() {
    val modelLoader: ModelLoader by inject()

    get(Routes.PROFILE.route) {
        val lang: Language =
            if (call.request.acceptLanguageItems().any { it.value.startsWith("de") }) {
                Language.DE
            } else {
                Language.EN
            }

        modelLoader.loadModel(
            serializer<Profile>(),
            "${lang.name.lowercase()}/profile.json",
        )?.let {
            call.respond(it)
        } ?: call.respond(
            HttpStatusCode.InternalServerError,
            "Internal Server Error",
        )
    }
}

fun Application.registerProfileRoutes() {
    routing {
        profileRouting()
    }
}
