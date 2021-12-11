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

package com.cmgapps.common.curriculumvitae.utils

import StubNetworkEmployment
import StubNetworkProfile
import StubNetworkSkill
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.headersOf
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

val mockClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            val responseHeaders =
                headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            when (request.url.encodedPath) {
                "/employment" -> respond(
                    Json.encodeToString(listOf(StubNetworkEmployment())),
                    headers = responseHeaders
                )
                "/profile" -> respond(
                    Json.encodeToString(StubNetworkProfile()),
                    headers = responseHeaders
                )
                "/skills" -> respond(
                    Json.encodeToString(listOf(StubNetworkSkill())),
                    headers = responseHeaders
                )
                // websockets not yet supported in MockEngine
                // https://youtrack.jetbrains.com/issue/KTOR-537
                //
                // "status" -> respond(
                //     Json.encodeToString(Status(1, 200, 300, 400)),
                //     headers = responseHeaders
                // )
                else -> error("Unhandled request: ${request.url}")
            }
        }
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}
