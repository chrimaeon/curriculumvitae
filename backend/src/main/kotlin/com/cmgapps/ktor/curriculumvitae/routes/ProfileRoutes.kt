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

import com.cmgapps.common.curriculumvitae.data.network.Profile
import com.cmgapps.ktor.curriculumvitae.Language
import com.cmgapps.ktor.curriculumvitae.ModelLoader
import com.cmgapps.ktor.curriculumvitae.Routes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.features.origin
import io.ktor.request.acceptLanguageItems
import io.ktor.request.host
import io.ktor.request.port
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.route
import io.ktor.routing.routing
import kotlinx.serialization.serializer

private fun Route.profileRouting(profiles: Map<Language, Profile>) {
    route(Routes.PROFILE.route) {
        get {
            val lang: Language =
                if (call.request.acceptLanguageItems().any { it.value.startsWith("de") }) {
                    Language.DE
                } else {
                    Language.EN
                }

            val host = with(call.request) {
                "${origin.scheme}://${host()}:${port()}"
            }

            val profile = profiles[lang]?.let {
                it.copy(profileImageUrl = it.profileImageUrl.replace("{{host}}", host))
            } ?: error("No profile found for $lang")
            call.respond(profile)
        }
    }
}

fun Application.registerProfileRoutes(modelLoader: ModelLoader) {
    modelLoader.loadModels(serializer<Profile>(), "profile.json").let {
        routing {
            profileRouting(it)
        }
    }
}
