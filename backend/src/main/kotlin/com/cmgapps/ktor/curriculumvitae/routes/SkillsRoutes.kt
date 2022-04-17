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
import com.cmgapps.ktor.curriculumvitae.infra.di.inject
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.serialization.serializer

private fun Route.skillsRouting() {
    val modelLoader: ModelLoader by inject()
    val skills: List<Skill> =
        modelLoader.loadModel(serializer(), "skills.json") ?: error("Cannot load skills")

    get(Routes.SKILLS.route) {
        call.respond(skills)
    }
}

fun Application.registerSkillRoutes() {
    routing {
        skillsRouting()
    }
}
