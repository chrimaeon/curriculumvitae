/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * SPDX-License-Identifier: Apache-2.0
 */
@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.data.network.Skill
import com.cmgapps.ktor.curriculumvitae.ModelLoader
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
import kotlinx.serialization.serializer
import org.koin.ktor.ext.inject

private fun Route.skillsRouting() {
    val modelLoader: ModelLoader by inject()
    val skills: List<Skill> =
        modelLoader.loadModel(serializer(), "skills.json") ?: error("Cannot load skills")

    route(
        Routes.SKILLS.route,
        {
            tags = listOf("Skills")
        },
    ) {
        get({ documentation() }) {
            call.respond(skills)
        }
    }
}

private fun OpenApiRoute.documentation() {
    response {
        HttpStatusCode.OK to {
            body<List<Skill>> {
                example(
                    "Skills",
                    listOf(
                        Skill("Mobile Development", 5),
                        Skill("Android", 5),
                    ),
                )
            }
        }
    }
}

fun Application.registerSkillRoutes() {
    routing {
        skillsRouting()
    }
}
