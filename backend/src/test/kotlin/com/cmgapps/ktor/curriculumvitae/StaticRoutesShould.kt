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
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class StaticRoutesShould {

    @Test
    fun `return Content-Type image png`() = testApplication {
        val response = client.get("/assets/profile.png")
        assertThat(
            response.headers[HttpHeaders.ContentType],
            `is`(ContentType.Image.PNG.toString()),
        )
    }

    @Test
    fun `get favicon from root`() = testApplication {
        val response = client.get("/favicon.ico")
        assertThat(
            response.status,
            `is`(HttpStatusCode.OK),
        )
    }
}
