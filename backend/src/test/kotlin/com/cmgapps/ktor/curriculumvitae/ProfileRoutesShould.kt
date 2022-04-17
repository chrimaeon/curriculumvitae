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

import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.withCharset
import io.ktor.server.testing.testApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class ProfileRoutesShould {

    @Test
    fun `return Content-Type application json`() = testApplication {
        val response = client.get(Routes.PROFILE.route)
        assertThat(
            response.headers[HttpHeaders.ContentType],
            `is`(ContentType.Application.Json.withCharset(Charsets.UTF_8).toString()),
        )
    }

    @Test
    fun `return default profile in EN`() = testApplication {
        val response = client.get(Routes.PROFILE.route)
        assertThat(
            response.bodyAsText(),
            `is`(
                "{" +
                    "\"name\":\"Firstname Lastname\"," +
                    "\"phone\":\"+1234567890\"," +
                    "\"profileImagePath\":\"/profile.png\"," +
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
                    "}",
            ),
        )
    }

    @Test
    fun `return profile in DE`() = testApplication {
        val response = client.get(Routes.PROFILE.route) {
            header(HttpHeaders.AcceptLanguage, "de")
        }

        assertThat(
            response.bodyAsText(),
            `is`(
                "{" +
                    "\"name\":\"Vorname Nachname\"," +
                    "\"phone\":\"+1234567890\"," +
                    "\"profileImagePath\":\"/profile.png\"," +
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
                    "}",
            ),
        )
    }

    @Test
    fun `return profile in EN if lang not found`() = testApplication {
        val response = client.get(Routes.PROFILE.route) {
            header(HttpHeaders.AcceptLanguage, "fr")
        }
        assertThat(
            response.bodyAsText(),
            `is`(
                "{" +
                    "\"name\":\"Firstname Lastname\"," +
                    "\"phone\":\"+1234567890\"," +
                    "\"profileImagePath\":\"/profile.png\"," +
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
                    "}",
            ),
        )
    }
}
