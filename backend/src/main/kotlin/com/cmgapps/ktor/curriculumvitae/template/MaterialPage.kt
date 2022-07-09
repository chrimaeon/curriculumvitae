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

package com.cmgapps.ktor.curriculumvitae.template

import io.ktor.html.Placeholder
import io.ktor.html.Template
import io.ktor.html.insert
import io.ktor.http.ContentType
import kotlinx.html.DIV
import kotlinx.html.HEAD
import kotlinx.html.HTML
import kotlinx.html.ScriptType
import kotlinx.html.body
import kotlinx.html.div
import kotlinx.html.head
import kotlinx.html.header
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.script
import kotlinx.html.span
import kotlinx.html.styleLink
import kotlinx.html.title

class MaterialPage(private val pageTitle: String = "") : Template<HTML> {
    val head = Placeholder<HEAD>()
    val content = Placeholder<DIV>()
    override fun HTML.apply() {
        this.attributes["lang"] = "en"
        head {
            title {
                +pageTitle
            }

            meta(name = "viewport", content = "width=device-width, initial-scale=1.0")

            link(
                rel = "preconnect",
                href = "https://fonts.gstatic.com",
            )

            styleLink("https://fonts.googleapis.com/icon?family=Material+Icons")
            styleLink("https://code.getmdl.io/1.3.0/material.light_blue-amber.min.css")
            styleLink("https://fonts.googleapis.com/css2?family=Roboto+Mono&family=Roboto:wght@300;400;500;700&display=swap")
            listOf("16x16", "32x32", "96x96").forEach {
                link(
                    rel = "icon",
                    type = ContentType.Image.PNG.toString(),
                    href = "/assets/favicon-$it.png",
                ) {
                    attributes["sizes"] = it
                }
            }

            link(
                rel = "icon",
                type = ContentType.Image.XIcon.toString(),
                href = "/assets/favicon.ico",
            )

            script(
                type = ScriptType.textJavaScript,
                src = "https://code.getmdl.io/1.3.0/material.min.js",
            ) {
                attributes["defer"] = ""
            }

            insert(this@MaterialPage.head)
        }
        body {
            div(classes = "mdl-layout mdl-js-layout mdl-layout--fixed-header") {
                header(classes = "mdl-layout__header") {
                    div(classes = "mdl-layout__header-row") {
                        span(classes = "mdl-layout-title") {
                            +pageTitle
                        }
                    }
                }
                main(classes = "mdl-layout__content") {
                    div(classes = "page-content") {
                        insert(content)
                    }
                }
            }
        }
    }
}
