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
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.css.CSSBuilder
import kotlinx.css.margin
import kotlinx.css.px
import kotlinx.html.DIV
import kotlinx.html.body
import kotlinx.html.code
import kotlinx.html.div
import kotlinx.html.h5
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.li
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.style
import kotlinx.html.title
import kotlinx.html.ul
import kotlinx.html.unsafe

private const val pageTitle = "Curriculum Vitae BFF"

fun Route.rootRouting() {
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
                    href = "http://fonts.googleapis.com/css?family=Roboto:300,400,500,700",
                    type = "text/css"
                )
                script(src = "https://code.getmdl.io/1.3.0/material.min.js") {}
                title(pageTitle)

                style {
                    unsafe {
                        +CSSBuilder().apply {
                            rule(".page-content") {
                                margin(16.px)
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

fun DIV.headerBar() {
    header(classes = "mdl-layout__header") {
        div(classes = "mdl-layout__header-row") {
            span(classes = "mdl-layout-title") {
                +pageTitle
            }
        }
    }
}

fun DIV.content() {
    main(classes = "mdl-layout__content") {
        div(classes = "page-content") {
            h5(classes = "headline") {
                +"Available API's"
            }
            ul {
                li {
                    code {
                        +"GET /profile?lang={lang}"
                    }
                }
            }
        }
    }
}

fun Application.registerRootRouting() {
    routing {
        rootRouting()
    }
}
