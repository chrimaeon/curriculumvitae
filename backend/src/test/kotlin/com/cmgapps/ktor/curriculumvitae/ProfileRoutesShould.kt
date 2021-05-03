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

package com.cmgapps.ktor.curriculumvitae

import com.cmgapps.shared.curriculumvitae.data.network.Address
import com.cmgapps.shared.curriculumvitae.data.network.Profile
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.withCharset
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class ProfileRoutesShould {

    @Mock
    lateinit var modelLoader: ModelLoader

    // Workaround for non-null parameter
    private fun <T> anyObject(): T {
        return any()
    }

    @Test
    fun `return Content-Type application json`() =
        withTestApplication(moduleFunction = { module() }) {
            with(handleRequest(HttpMethod.Get, Routes.PROFILE.route)) {
                assertThat(
                    response.headers[HttpHeaders.ContentType],
                    `is`(ContentType.Application.Json.withCharset(Charsets.UTF_8).toString())
                )
            }
        }

    @Test
    fun `return profile`() {
        val profile = Profile(
            name = "My Name",
            phone = "+1234567890",
            profileImageUrl = "{{host}}/assets/profile.png",
            address = Address(
                street = "Street 43",
                city = "No Where",
                postalCode = "90210"
            ),
            email = "me@home.at",
            intro = listOf(
                "Line 1",
                "Line 2",
                "etc ... etc ... etc"
            )
        )

        `when`(modelLoader.loadModels<Profile>(anyObject(), anyString())).thenReturn(
            mapOf(
                Language.EN to profile,
                Language.DE to profile
            )
        )

        withTestApplication(moduleFunction = { module(modelLoader) }) {
            with(handleRequest(HttpMethod.Get, Routes.PROFILE.route)) {
                assertThat(
                    response.content,
                    `is`(
                        "{" +
                            "\"name\":\"My Name\"," +
                            "\"phone\":\"+1234567890\"," +
                            "\"profileImageUrl\":\"http://localhost:80/assets/profile.png\"," +
                            "\"address\":{" +
                            "\"street\":\"Street 43\"," +
                            "\"city\":\"No Where\"," +
                            "\"postalCode\":\"90210\"" +
                            "}," +
                            "\"email\":\"me@home.at\"," +
                            "\"intro\":[" +
                            "\"Line 1\"," +
                            "\"Line 2\"," +
                            "\"etc ... etc ... etc\"" +
                            "]" +
                            "}"
                    )
                )
            }
        }
    }
}
