/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.network.Employment
import com.cmgapps.ktor.curriculumvitae.Routes
import com.cmgapps.ktor.curriculumvitae.infra.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.datetime.LocalDate

fun Route.employmentRouting() {
    val database: CvDatabase by inject()
    get(Routes.EMPLOYMENT.route) {
        call.respond(database.employmentQueries.selectAll(::mapper).executeAsList())
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

fun Application.registerEmploymentRoutes() {
    routing {
        employmentRouting()
    }
}
