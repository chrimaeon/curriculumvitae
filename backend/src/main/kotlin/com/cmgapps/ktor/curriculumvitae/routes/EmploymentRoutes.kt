/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.cmgapps.ktor.curriculumvitae.Routes
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate
import org.koin.ktor.ext.inject

fun Route.employmentRouting() {
    val database: CvDatabase by inject()
    route(
        Routes.EMPLOYMENT.route,
        {
            tags = listOf("Employment")
        },
    ) {
        get({ documentation() }) {
            call.respond(database.employmentQueries.selectAll(::mapper).executeAsList())
        }
    }
}

private fun mapper(
    id: Int,
    jobTitle: String,
    employer: String,
    startDate: String,
    endDate: String?,
    city: String,
    description: List<String>,
) = Employment(
    id,
    jobTitle,
    employer,
    LocalDate.parse(startDate),
    endDate?.let { LocalDate.parse(it) },
    city,
    description,
)

private fun OpenApiRoute.documentation() {
    response {
        HttpStatusCode.OK to {
            description = "Success"
            body<Employment> {
                example(
                    "Employment",
                    Employment(
                        id = 1,
                        jobTitle = "Software Developer",
                        employer = "CMG Mobile Apps",
                        startDate = LocalDate.parse("2010-06-01"),
                        endDate = null,
                        city = "Graz",
                        description = listOf(
                            "Founder",
                            "Software development",
                        ),
                    ),
                )
            }
        }
    }
}

fun Application.registerEmploymentRoutes() {
    routing {
        employmentRouting()
    }
}
