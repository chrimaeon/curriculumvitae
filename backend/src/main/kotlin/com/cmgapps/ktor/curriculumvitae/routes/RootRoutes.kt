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

import com.cmgapps.ktor.curriculumvitae.Routes
import com.cmgapps.ktor.curriculumvitae.template.MaterialPage
import com.cmgapps.shared.curriculumvitae.data.network.Address
import com.cmgapps.shared.curriculumvitae.data.network.Employment
import com.cmgapps.shared.curriculumvitae.data.network.Profile
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtmlTemplate
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.CSSBuilder
import kotlinx.css.Color
import kotlinx.css.FontWeight
import kotlinx.css.Overflow
import kotlinx.css.WhiteSpace
import kotlinx.css.borderRadius
import kotlinx.css.color
import kotlinx.css.fontFamily
import kotlinx.css.fontWeight
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.overflowX
import kotlinx.css.padding
import kotlinx.css.pct
import kotlinx.css.properties.TextDecoration
import kotlinx.css.px
import kotlinx.css.textDecoration
import kotlinx.css.whiteSpace
import kotlinx.css.width
import kotlinx.html.DIV
import kotlinx.html.STYLE
import kotlinx.html.a
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.h6
import kotlinx.html.pre
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.tr
import kotlinx.html.unsafe
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.util.Locale

private fun STYLE.cssRules(rules: CSSBuilder.() -> Unit) {
    this.unsafe {
        +CSSBuilder().apply(rules).toString()
    }
}

private fun Route.rootRouting() {
    get(Routes.ROOT.route) {
        call.respondHtmlTemplate(MaterialPage("Curriculum Vitae BFF")) {
            head {
                style(type = ContentType.Text.CSS.toString()) {
                    cssRules {
                        kotlinx.css.pre {
                            fontFamily = "'Roboto Mono', monospace"
                            overflowX = Overflow.scroll
                        }
                        ".white-space-normal" {
                            whiteSpace = WhiteSpace.normal
                        }
                        ".page-content" {
                            margin(16.px)
                        }
                        ".api-card" {
                            width = 100.pct
                        }
                        ".api-card .mdl-card__title-text" {
                            marginLeft = 16.px
                        }
                        ".api-card .mdl-card__title-text a" {
                            color = Color.inherit
                            fontWeight = FontWeight.inherit
                            textDecoration = TextDecoration.none
                        }
                        ".api-table-container" {
                            overflowX = Overflow.scroll
                        }
                        ".api-table" {
                            margin(horizontal = 16.px)
                        }

                        ".api-response" {
                            margin(horizontal = 16.px)
                        }

                        ".api-response pre" {
                            whiteSpace = WhiteSpace.preWrap
                            padding(8.px)
                            borderRadius = 4.px
                        }
                    }
                }
            }
            content {
                page()
            }
        }
    }
}

private fun DIV.page() {
    h5(classes = "headline") {
        +"Available API's"
    }

    val langParam = Param(
        "Accept-Language",
        String::class.java,
        Where.HEADER,
        "A HTTP language header; see https://datatracker.ietf.org/doc/html/rfc7231#section-5.3.5"
    )

    div(classes = "mdl-grid") {
        apiCard(
            HttpMethod.Get,
            "/profile",
            listOf(langParam),
            Profile(
                name = "My Name",
                phone = "+1234567890",
                profileImageUrl = "https://via.placeholder.com/150",
                address = Address(
                    street = "Main Street 1",
                    city = "My Hometown",
                    postalCode = "42"
                ),
                email = "noreply@test.com",
                intro = listOf(
                    "Intro line 1 ...",
                    "Intro line 2 ...",
                    "etc ... etc ... etc ..."
                ),
            )
        )
        apiCard(
            HttpMethod.Get,
            "/employment",
            listOf(langParam),
            Employment(
                jobTitle = "Software Developer",
                employer = "CMG Mobile Apps",
                startDate = LocalDate.parse("2010-06-01"),
                endDate = null,
                city = "Graz",
                description = listOf(
                    "Founder",
                    "Software development"
                )
            )
        )
    }
}

private enum class Where {
    QUERY, HEADER, PATH;

    override fun toString(): String = name.lowercase(Locale.US)
        .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.US) else it.toString() }
}

private data class Param(val name: String, val type: Class<*>, val `in`: Where, val desc: String)

@OptIn(ExperimentalSerializationApi::class)
private val json = Json {
    prettyPrint = true
    prettyPrintIndent = "  "
}

@OptIn(ExperimentalSerializationApi::class)
private inline fun <reified T> DIV.apiCard(
    method: HttpMethod,
    route: String,
    params: List<Param>? = null,
    response: T? = null
) {
    div(classes = "mdl-cell mdl-cell--6-col mdl-cell--8-col-tablet") {
        div(classes = "api-card mdl-card mdl-shadow--2dp") {
            div(classes = "mdl-card__title") {
                span(classes = "mdl-chip mdl-color--accent") {
                    span(classes = "mdl-chip__text") {
                        +method.value
                    }
                }
                h3(classes = "mdl-card__title-text") {
                    a(href = route) {
                        +route
                    }
                }
            }
            params?.let { paramsTable(it) }
            response?.let { response(it) }
        }
    }
}

private const val NON_NUMERIC_TABLE_CELL = "mdl-data-table__cell--non-numeric"

private fun DIV.paramsTable(params: List<Param>) {
    div(classes = "api-table-container") {
        table(classes = "api-table mdl-data-table") {
            thead {
                tr {
                    th(classes = NON_NUMERIC_TABLE_CELL) {
                        +"Name"
                    }
                    th(classes = NON_NUMERIC_TABLE_CELL) {
                        +"Type"
                    }
                    th(classes = NON_NUMERIC_TABLE_CELL) {
                        +"In"
                    }
                    th(classes = NON_NUMERIC_TABLE_CELL) {
                        +"Description"
                    }
                }
            }
            params.forEach { param ->
                tbody {
                    tr {
                        td(classes = NON_NUMERIC_TABLE_CELL) {
                            span(classes = "mdl-chip") {
                                span(classes = "mdl-chip__text") {
                                    +param.name
                                }
                            }
                        }
                        td(classes = NON_NUMERIC_TABLE_CELL) {
                            +param.type.simpleName
                        }
                        td(classes = NON_NUMERIC_TABLE_CELL) {
                            +param.`in`.toString()
                        }
                        td(classes = "$NON_NUMERIC_TABLE_CELL white-space-normal") {
                            +param.desc
                        }
                    }
                }
            }
        }
    }
}

private inline fun <reified T> DIV.response(t: T) {
    div(classes = "api-response") {
        h6(classes = "mdl-typography-subhead") {
            +"Response"
        }
        pre(classes = "mdl-color--grey-200") {
            +json.encodeToString(t)
        }
    }
}

internal fun Application.registerRootRouting() {
    routing {
        rootRouting()
    }
}
