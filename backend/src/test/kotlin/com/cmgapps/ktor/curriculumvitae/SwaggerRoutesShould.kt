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
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class SwaggerRoutesShould {

    @Test
    fun `return OK on GET`() = testApplication {
        val response = client.get("/swagger")
        assertThat(response.status, `is`(HttpStatusCode.OK))
    }

    @Disabled(
        "Flaky because if inconsistent `Instant` and `LocalDate`" +
            " parsing: https://github.com/SMILEY4/ktor-swagger-ui/issues/44",
    )
    @Test
    fun `return html on GET`() = testApplication {
        val apiInfo = BuildConfig.apiInfo
        val replacementInfo = mapOf(
            "\$\$__NAME__\$\$" to apiInfo.contactName,
            "\$\$__EMAIL__\$\$" to apiInfo.contactEmail,
            "\$\$__SERVER_PRODUCTION_URL__\$\$" to apiInfo.serverUrl,
            "\$\$__SERVER_PRODUCTION_DESCRIPTION__\$\$" to apiInfo.serverDescription,
        )
        val expected = javaClass.classLoader.getResource("openapi.json")?.readText()?.let {
            replacementInfo.entries.fold(it) { acc, (key, value) -> acc.replace(key, value) }
        }

        val response = client.get("/swagger/api.json")
        assertThat(
            response.bodyAsText(),
            `is`(expected),
        )
    }
}
