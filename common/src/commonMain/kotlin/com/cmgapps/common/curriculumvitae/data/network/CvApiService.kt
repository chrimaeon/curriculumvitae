/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.HttpMethod
import io.ktor.http.URLBuilder
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.path
import io.ktor.utils.io.ByteReadChannel
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class CvApiService(private val client: HttpClient, private val baseUrl: Url) {
    suspend fun getProfile(): Profile = client.get(
        URLBuilder(baseUrl).apply {
            path("profile")
        }.build(),
    ).body()

    suspend fun getEmployments(): List<Employment> = client.get(
        URLBuilder(baseUrl).apply {
            path("employment")
        }.build(),
    ).body()

    suspend fun getSkills(): List<Skill> = client.get(
        URLBuilder(baseUrl).apply {
            path("skills")
        }.build(),
    ).body()

    suspend fun getAsset(assetPath: String): ByteReadChannel = client.get(
        URLBuilder(baseUrl).apply {
            path(assetPath)
        }.build(),
    ).bodyAsChannel()

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
            },
        ) {
            while (!incoming.isClosedForReceive) {
                val status = incoming.receive() as Frame.Text
                emit(Json.decodeFromString(status.readText()))
            }
        }
    }
}
