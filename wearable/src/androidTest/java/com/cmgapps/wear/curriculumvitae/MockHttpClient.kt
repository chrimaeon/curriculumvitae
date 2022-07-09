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

@file:Suppress("TestFunctionName")

package com.cmgapps.wear.curriculumvitae

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

fun MockHttpClient(): HttpClient = HttpClient(MockEngine) {
    engine {
        addHandler { request ->
            val responseHeaders =
                headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            when (request.url.encodedPath) {
                "/profile" -> respond(
                    Json.encodeToString(StubNetworkProfile()),
                    headers = responseHeaders,
                )
                "/employment" -> respond(
                    Json.encodeToString(listOf(StubNetworkEmployment())),
                    headers = responseHeaders,
                )
                "/skills" -> respond(
                    Json.encodeToString(listOf(StubNetworkSkill())),
                    headers = responseHeaders,
                )
                else -> error("Unhandled request: ${request.url}")
            }
        }
    }
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
}
