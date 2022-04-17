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
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.testApplication
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class RootRoutesShould {

    @Test
    fun `return OK on GET`() = testApplication {
        val response = client.get(Routes.ROOT.route)
        assertThat(response.status, `is`(HttpStatusCode.OK))
    }

    @Suppress("BlockingMethodInNonBlockingContext")
    @Test
    fun `return html on GET`() = testApplication {
        val expected = javaClass.classLoader.getResourceAsStream("root.html")?.use {
            String(it.readAllBytes())
        } ?: error("resource not found")

        val response = client.get(Routes.ROOT.route)
        assertThat(
            response.bodyAsText(),
            `is`(expected),
        )
    }
}
