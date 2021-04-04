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

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.html.respondHtml
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.CSSBuilder
import kotlinx.css.WhiteSpace
import kotlinx.css.height
import kotlinx.css.margin
import kotlinx.css.marginLeft
import kotlinx.css.pct
import kotlinx.css.px
import kotlinx.css.whiteSpace
import kotlinx.css.width
import kotlinx.html.DIV
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.h3
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.table
import kotlinx.html.tbody
import kotlinx.html.td
import kotlinx.html.th
import kotlinx.html.thead
import kotlinx.html.title
import kotlinx.html.tr
import kotlinx.html.unsafe
import java.util.Locale

private const val pageTitle = "Curriculum Vitae BFF"

private fun Route.rootRouting() {
    get("/") {
        call.respondHtml {
            head {
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/icon?family=Material+Icons",
                    type = "text/css"
                )
                link(
                    rel = "stylesheet",
                    href = "https://code.getmdl.io/1.3.0/material.light_blue-amber.min.css",
                    type = "text/css"
                )
                link(
                    rel = "stylesheet",
                    href = "https://fonts.googleapis.com/css?family=Roboto:300,400,500,700",
                    type = "text/css"
                )

                listOf("16x16", "32x32", "96x96").forEach {
                    link(
                        rel = "icon",
                        type = ContentType.Image.PNG.toString(),
                        href = "/assets/favicon-$it.png"
                    ) {
                        attributes["sizes"] = it
                    }
                }

                link(
                    rel = "icon",
                    type = ContentType.Image.XIcon.toString(),
                    href = "/assets/favicon.ico"
                )
                script(src = "https://code.getmdl.io/1.3.0/material.min.js") {
                    attributes["defer"] = ""
                }
                title(pageTitle)

                style {
                    unsafe {
                        +CSSBuilder().apply {
                            rule(".white-space-normal") {
                                whiteSpace = WhiteSpace.normal
                            }
                            rule(".page-content") {
                                margin(16.px)
                            }
                            rule(".api-card") {
                                width = 100.pct
                            }
                            rule(".api-card .mdl-card__title-text") {
                                marginLeft = 16.px
                            }
                            rule(".spacer") {
                                height = 16.px
                                width = 100.pct
                            }
                            rule(".api-table") {
                                margin(horizontal = 16.px)
                            }
                        }.toString()
                    }
                }
            }
            body {
                div(classes = "mdl-layout mdl-js-layout mdl-layout--fixed-header") {
                    headerBar()
                    content()
                }
            }
        }
    }
}

private fun DIV.headerBar() {
    header(classes = "mdl-layout__header") {
        div(classes = "mdl-layout__header-row") {
            span(classes = "mdl-layout-title") {
                +pageTitle
            }
        }
    }
}

private fun DIV.content() {
    main(classes = "mdl-layout__content") {
        div(classes = "page-content") {
            h5(classes = "headline") {
                +"Available API's"
            }

            val langParam = Param(
                "lang",
                String::class.java,
                Where.QUERY,
                "The language to return the answer; Defaults to 'en'"
            )

            div(classes = "mdl-grid") {
                apiCard(
                    HttpMethod.Get,
                    "/profile",
                    listOf(langParam)
                )
                apiCard(HttpMethod.Get, "/skills", listOf(langParam))
            }

        }
    }
}

private enum class Where {
    QUERY, HEADER, PATH;

    override fun toString(): String = name.toLowerCase(Locale.US)
}

private data class Param(val name: String, val type: Class<*>, val `in`: Where, val desc: String)

private fun DIV.apiCard(method: HttpMethod, route: String, params: List<Param>? = null) {
    div(classes = "mdl-cell mdl-cell--6-col mdl-cell--8-col-tablet") {
        div(classes = "api-card mdl-card mdl-shadow--2dp") {
            div(classes = "mdl-card__title") {
                span(classes = "mdl-chip mdl-color--accent") {
                    span(classes = "mdl-chip__text") {
                        +method.value
                    }
                }
                h3(classes = "mdl-card__title-text") {
                    +route
                }
            }
            params?.let {
                table(classes = "api-table mdl-data-table") {
                    thead {
                        tr {
                            th(classes = "mdl-data-table__cell--non-numeric") {
                                +"Name"
                            }
                            th(classes = "mdl-data-table__cell--non-numeric") {
                                +"Type"
                            }
                            th(classes = "mdl-data-table__cell--non-numeric") {
                                +"In"
                            }
                            th(classes = "mdl-data-table__cell--non-numeric") {
                                +"Description"
                            }
                        }
                    }
                    it.forEach { param ->
                        tbody {
                            tr {
                                td(classes = "mdl-data-table__cell--non-numeric") {
                                    span(classes = "mdl-chip") {
                                        span(classes = "mdl-chip__text") {
                                            +param.name
                                        }
                                    }
                                }
                                td(classes = "mdl-data-table__cell--non-numeric") {
                                    +param.type.simpleName
                                }
                                td(classes = "mdl-data-table__cell--non-numeric") {
                                    +param.`in`.toString()
                                }
                                td(classes = "mdl-data-table__cell--non-numeric white-space-normal") {
                                    +param.desc
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

internal fun Application.registerRootRouting() {
    routing {
        rootRouting()
    }
}
