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

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapp.shared.curriculumvitae.data.network.Address
import com.cmgapp.shared.curriculumvitae.data.network.Profile
import com.cmgapps.ktor.curriculumvitae.Routes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing

fun Route.profileRouting() {
    route(Routes.PROFILE.route) {
        get {
            val lang = call.request.queryParameters["lang"] ?: "en"
            val profileUrl = with(call.request) {
                "${origin.scheme}://${host()}:${port()}/assets/profile.png"
            }
            call.respond(
                Profile(
                    name = "My Name",
                    phone = "+1234567890",
                    profileImageUrl = profileUrl,
                    address = Address(
                        street = "Street 43",
                        city = "No Where",
                        postalCode = "90210"
                    ),
                    email = "me@home.at",
                    intro = listOf(
                        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy" +
                            " eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed" +
                            " diam voluptua. At vero eos et accusam et",
                        "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy" +
                            " eirmod tempor invidunt ut labore et dolore magna aliquyam",
                    ),
                    lang = lang
                )
            )
        }
    }
}

fun Application.registerProfileRoutes() {
    routing {
        profileRouting()
    }
}
