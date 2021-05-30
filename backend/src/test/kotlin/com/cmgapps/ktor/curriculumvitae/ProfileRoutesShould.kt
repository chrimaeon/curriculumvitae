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

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.withCharset
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class ProfileRoutesShould {

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
    fun `return default profile in EN`() {
        withTestApplication(moduleFunction = { module() }) {
            with(handleRequest(HttpMethod.Get, Routes.PROFILE.route)) {
                assertThat(
                    response.content,
                    `is`(
                        "{" +
                            "\"name\":\"Firstname Lastname\"," +
                            "\"phone\":\"+1234567890\"," +
                            "\"profileImageUrl\":\"http://localhost:80/assets/profile.png\"," +
                            "\"address\":{" +
                            "\"street\":\"Homestreet 1\"," +
                            "\"city\":\"City\"," +
                            "\"postalCode\":\"42\"" +
                            "}," +
                            "\"email\":\"me@home.us\"," +
                            "\"intro\":[" +
                            "\"Line 1\"," +
                            "\"Line 2\"" +
                            "]" +
                            "}"
                    )
                )
            }
        }
    }

    @Test
    fun `return profile in DE`() {
        withTestApplication(moduleFunction = { module() }) {
            with(
                handleRequest(HttpMethod.Get, Routes.PROFILE.route) {
                    addHeader(HttpHeaders.AcceptLanguage, "de")
                }
            ) {
                assertThat(
                    response.content,
                    `is`(
                        "{" +
                            "\"name\":\"Vorname Nachname\"," +
                            "\"phone\":\"+1234567890\"," +
                            "\"profileImageUrl\":\"http://localhost:80/assets/profile.png\"," +
                            "\"address\":{" +
                            "\"street\":\"Heimatstraße 1\"," +
                            "\"city\":\"Stadt\"," +
                            "\"postalCode\":\"42\"" +
                            "}," +
                            "\"email\":\"email@home.at\"," +
                            "\"intro\":[" +
                            "\"Zeile 1\"," +
                            "\"Zeile 2\"" +
                            "]" +
                            "}"
                    )
                )
            }
        }
    }

    @Test
    fun `return profile in EN if lang not found`() {
        withTestApplication(moduleFunction = { module() }) {
            with(handleRequest(HttpMethod.Get, Routes.PROFILE.route + "?lang=fr")) {
                assertThat(
                    response.content,
                    `is`(
                        "{" +
                            "\"name\":\"Firstname Lastname\"," +
                            "\"phone\":\"+1234567890\"," +
                            "\"profileImageUrl\":\"http://localhost:80/assets/profile.png\"," +
                            "\"address\":{" +
                            "\"street\":\"Homestreet 1\"," +
                            "\"city\":\"City\"," +
                            "\"postalCode\":\"42\"" +
                            "}," +
                            "\"email\":\"me@home.us\"," +
                            "\"intro\":[" +
                            "\"Line 1\"," +
                            "\"Line 2\"" +
                            "]" +
                            "}"
                    )
                )
            }
        }
    }
}
