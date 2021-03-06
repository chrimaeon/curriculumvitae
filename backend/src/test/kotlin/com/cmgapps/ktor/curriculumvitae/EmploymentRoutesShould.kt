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

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.EmploymentQueries
import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.squareup.sqldelight.Query
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.withCharset
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import kotlinx.datetime.LocalDate
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.dsl.module
import org.koin.ktor.ext.modules
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class EmploymentRoutesShould {

    @Mock
    private lateinit var databaseMock: CvDatabase

    @Mock
    private lateinit var employmentQueriesMock: EmploymentQueries

    @Test
    fun `return Content-Type application json`() =
        withTestApplication(moduleFunction = { module() }) {
            with(handleRequest(HttpMethod.Get, Routes.EMPLOYMENT.route)) {
                assertThat(
                    response.headers[HttpHeaders.ContentType],
                    `is`(ContentType.Application.Json.withCharset(Charsets.UTF_8).toString())
                )
            }
        }

    @Test
    fun `return employment`() {
        val queryMock = mock<Query<Employment>> {
            on { executeAsList() } doReturn listOf(
                Employment(
                    "Software Developer",
                    "CMG Mobile Apps",
                    LocalDate.parse("2010-06-01"),
                    null,
                    "Graz",
                    listOf("Founder", "Software development")
                )
            )
        }
        whenever(employmentQueriesMock.selectAll<Employment>(any())) doReturn queryMock
        whenever(databaseMock.employmentQueries) doReturn employmentQueriesMock

        withTestApplication(moduleFunction = {
            module()
        }) {
            application.modules(
                module {
                    single { databaseMock }
                }
            )

            with(handleRequest(HttpMethod.Get, Routes.EMPLOYMENT.route)) {

                assertThat(
                    response.content,
                    `is`(
                        "[" +
                            "{" +
                            "\"jobTitle\":\"Software Developer\"," +
                            "\"employer\":\"CMG Mobile Apps\"," +
                            "\"startDate\":\"2010-06-01\"," +
                            "\"endDate\":null," +
                            "\"city\":\"Graz\"," +
                            "\"description\":" +
                            "[" +
                            "\"Founder\"," +
                            "\"Software development\"" +
                            "]" +
                            "}" +
                            "]"
                    )
                )
            }
        }
    }
}
