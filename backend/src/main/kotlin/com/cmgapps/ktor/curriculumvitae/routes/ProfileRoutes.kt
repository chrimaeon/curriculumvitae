/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.network.Address
import com.cmgapps.common.curriculumvitae.data.network.Profile
import com.cmgapps.ktor.curriculumvitae.Language
import com.cmgapps.ktor.curriculumvitae.ModelLoader
import com.cmgapps.ktor.curriculumvitae.Routes
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.request.acceptLanguageItems
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import kotlinx.serialization.serializer
import org.koin.ktor.ext.inject

private fun Route.profileRouting() {
    val modelLoader: ModelLoader by inject()

    route(
        Routes.PROFILE.route,
        {
            tags = listOf("Profile")
        },
    ) {
        get({ documentation() }) {
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
}

private fun OpenApiRoute.documentation() {
    request {
        headerParameter<String>(HttpHeaders.AcceptLanguage) {
            description =
                "A HTTP language header; see https://datatracker.ietf.org/doc/html/rfc7231#section-5.3.5"
        }
    }
    response {
        HttpStatusCode.OK to {
            description = "Success"
            body<Profile> {
                example(
                    "Profile",
                    Profile(
                        name = "My Name",
                        phone = "+1234567890",
                        profileImagePath = "/profile.png",
                        address = Address(
                            street = "Main Street 1",
                            city = "My Hometown",
                            postalCode = "42",
                        ),
                        email = "noreply@test.com",
                        intro = listOf(
                            "Intro line 1 ...",
                            "Intro line 2 ...",
                            "etc ... etc ... etc ...",
                        ),
                    ),
                )
            }
        }
    }
}

fun Application.registerProfileRoutes() {
    routing {
        profileRouting()
    }
}
