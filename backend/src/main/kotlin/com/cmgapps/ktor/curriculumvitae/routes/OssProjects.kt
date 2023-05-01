/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 */

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.GithubReposUrl
import com.cmgapps.common.curriculumvitae.data.network.OssProject
import com.cmgapps.ktor.curriculumvitae.IgnoreKeysJson
import com.cmgapps.ktor.curriculumvitae.Routes
import com.cmgapps.ktor.curriculumvitae.infra.model.GithubUserRepository
import com.cmgapps.ktor.curriculumvitae.infra.model.asOssProject
import io.ktor.http.URLBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.koin.ktor.ext.inject
import java.io.IOException
import java.net.URL

@OptIn(ExperimentalSerializationApi::class)
private fun Route.ossProjectsRoute() {
    val ioDispatcher: CoroutineDispatcher by inject()

    get(Routes.OSS_PROJECTS.route) {
        @Suppress("BlockingMethodInNonBlockingContext")
        val response: List<OssProject> = try {
            withContext(ioDispatcher) {
                generateUrl().openConnection().apply {
                    setRequestProperty("Accept", "application/vnd.github+json")
                }.getInputStream().use { stream ->
                    IgnoreKeysJson.decodeFromStream<List<GithubUserRepository>>(stream)
                }
            }
        } catch (exc: IOException) {
            call.application.log.error("Error fetching repos", exc)
            emptyList()
        }.map { it.asOssProject() }

        call.respond(response)
    }
}

private fun generateUrl() = URL(
    URLBuilder(GithubReposUrl).apply {
        parameters.append("sort", "pushed")
        parameters.append("direction", "desc")
        parameters.append("type", "owner")
    }.build().toString(),
)

internal fun Application.registerOssProjects() {
    routing {
        ossProjectsRoute()
    }
}
