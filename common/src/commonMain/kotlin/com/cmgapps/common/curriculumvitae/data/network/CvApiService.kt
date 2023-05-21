/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.websocket.ws
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.URLProtocol
import io.ktor.http.Url
import io.ktor.http.appendPathSegments
import io.ktor.utils.io.ByteReadChannel
import io.ktor.websocket.Frame
import io.ktor.websocket.readText
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class CvApiService(private val client: HttpClient, private val baseUrl: Url) {
    suspend fun getProfile(): Profile = client.get(baseUrl) {
        url {
            appendPathSegments("profile")
        }
    }.body()

    suspend fun getEmployments(): List<Employment> = client.get(baseUrl) {
        url {
            appendPathSegments("employment")
        }
    }.body()

    suspend fun getSkills(): List<Skill> = client.get(baseUrl) {
        url {
            appendPathSegments("skills")
        }
    }.body()

    suspend fun getAsset(assetPath: String): ByteReadChannel = client.get(baseUrl) {
        url {
            appendPathSegments(assetPath)
        }
    }.bodyAsChannel()

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getApiStatus(): Flow<Status> = flow {
        client.ws(
            baseUrl.toString(),
            request = {
                url {
                    appendPathSegments("status")
                    protocol =
                        if (url.protocol == URLProtocol.HTTPS) URLProtocol.WSS else URLProtocol.WS
                }
            },
        ) {
            while (!incoming.isClosedForReceive) {
                val status = incoming.receive() as Frame.Text
                emit(Json.decodeFromString(status.readText()))
            }
        }
    }

    suspend fun getOssProjects(): List<OssProject> = client.get(baseUrl) {
        url {
            appendPathSegments("oss-projects")
        }
    }.body()
}
