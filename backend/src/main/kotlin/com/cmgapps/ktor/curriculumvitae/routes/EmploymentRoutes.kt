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

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.cmgapps.ktor.curriculumvitae.Routes
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month

fun Route.employmentRouting() {
    get(Routes.EMPLOYMENT.route) {
        call.respond(
            listOf(
                Employment(
                    jobTitle = "Software Developer",
                    employer = "CMG Mobile Apps",
                    startDate = LocalDate(2010, Month.JUNE, 1),
                    endDate = null,
                    city = "Graz",
                    description = listOf(
                        "Founder",
                        "Software development"
                    )
                )
            )
        )
    }
}

fun Application.registerEmploymentRoutes() {
    routing {
        employmentRouting()
    }
}
