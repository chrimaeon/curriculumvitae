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

package com.cmgapps.common.curriculumvitae.data.network

import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CvApiService(private val client: HttpClient, private val baseUrl: Url) {
    suspend fun getProfile(): Profile = client.get(
        URLBuilder(baseUrl).apply {
            path("profile")
        }.build()
    )

    suspend fun getEmployments(): List<Employment> = client.get(
        URLBuilder(baseUrl).apply {
            path("employment")
        }.build()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getApiStatus(): Flow<Status> = flow {
        client.webSocket(
            HttpMethod.Get,
            baseUrl.host,
            baseUrl.port,
            "status",
            request = {
                url.protocol =
                    if (baseUrl.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS
            }
        ) {
            while (!incoming.isClosedForReceive) {
                val status = incoming.receive() as Frame.Text
                emit(Json.decodeFromString(status.readText()))
            }
        }
    }
}
