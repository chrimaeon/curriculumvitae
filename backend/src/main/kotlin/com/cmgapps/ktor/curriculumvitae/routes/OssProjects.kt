/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 */

package com.cmgapps.ktor.curriculumvitae.routes

import com.cmgapps.common.curriculumvitae.GithubReposUrl
import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.Ossproject
import com.cmgapps.common.curriculumvitae.data.network.OssProject
import com.cmgapps.common.curriculumvitae.data.network.asNetworkModel
import com.cmgapps.ktor.curriculumvitae.IgnoreKeysJson
import com.cmgapps.ktor.curriculumvitae.Routes
import com.cmgapps.ktor.curriculumvitae.infra.model.GithubUserRepository
import com.cmgapps.ktor.curriculumvitae.infra.model.asDatabaseOssProject
import com.cmgapps.ktor.curriculumvitae.infra.model.asOssProject
import io.github.smiley4.ktorswaggerui.dsl.OpenApiResponse
import io.github.smiley4.ktorswaggerui.dsl.OpenApiRoute
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.route
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.URLBuilder
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.routing
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.decodeFromStream
import org.koin.ktor.ext.inject
import java.io.IOException
import java.net.URL
import kotlin.time.Duration.Companion.days

@OptIn(ExperimentalSerializationApi::class)
private fun Route.ossProjectsRoute() {
    val ioDispatcher: CoroutineDispatcher by inject()
    val database: CvDatabase by inject()
    route(
        Routes.OSS_PROJECTS.route,
        { tags = listOf("Open-Source Projects") },
    ) {
        get(OpenApiRoute::documentation) {
            @Suppress("BlockingMethodInNonBlockingContext")
            val response: List<OssProject> = try {
                withContext(ioDispatcher) {
                    val lastChecked = database.lastCheckQueries.getTimestamp().executeAsOneOrNull()
                        ?: Instant.DISTANT_PAST
                    if (lastChecked.plus(1.days) < Clock.System.now()) {
                        try {
                            generateUrl().openConnection().apply {
                                setRequestProperty(
                                    HttpHeaders.Accept,
                                    "application/vnd.github+json",
                                )
                            }.getInputStream().use { stream ->
                                IgnoreKeysJson.decodeFromStream<List<GithubUserRepository>>(stream)
                            }.also { githubRepos ->
                                database.transaction {
                                    githubRepos.asSequence()
                                        .map(GithubUserRepository::asDatabaseOssProject)
                                        .forEach {
                                            database.ossProjectQueries.insertOssProject(it)
                                        }
                                }
                            }.map { it.asOssProject() }
                        } catch (exc: IOException) {
                            call.application.log.error("Error fetching repos", exc)
                            database.ossProjectQueries.selectAllOrderedByPushedAt()
                                .executeAsList().map(Ossproject::asNetworkModel)
                        }
                    } else {
                        database.ossProjectQueries.selectAllOrderedByPushedAt().executeAsList()
                            .map(Ossproject::asNetworkModel)
                    }
                }
            } catch (exc: IOException) {
                call.application.log.error("Error fetching repos", exc)
                emptyList()
            }

            call.respond(response)
        }
    }
}

private fun OpenApiRoute.documentation() {
    fun OpenApiResponse.defaultExample() {
        description = "Success"
        body<List<OssProject>> {
            example(
                "OSS-Projects",
                listOf(
                    OssProject(
                        name = "my-project",
                        description = "My Open Source Project",
                        url = "https://cmgapps.com",
                        topics = listOf(
                            "kotlin",
                            "android",
                            "kotlin multiplatform",
                        ),
                        stars = 42,
                        private = false,
                        fork = false,
                        archived = false,
                        pushedAt = Instant.parse("1979-09-02T18:00:00Z"),
                    ),
                ),
            )
            example(
                "OSS-Projects with null `pushedAt`",
                listOf(
                    OssProject(
                        name = "my-project",
                        description = "My Open Source Project",
                        url = "https://cmgapps.com",
                        topics = listOf(
                            "kotlin",
                            "android",
                            "kotlin multiplatform",
                        ),
                        stars = 42,
                        private = false,
                        fork = false,
                        archived = false,
                        pushedAt = null,
                    ),
                ),
            )
        }
    }
    response {
        default(OpenApiResponse::defaultExample)
        HttpStatusCode.OK to OpenApiResponse::defaultExample
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
