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
@file:Suppress("ktlint:filename")

package com.cmgapps.web.curriculumvitae.ui

import PRODUCTION
import androidx.compose.runtime.Composable
import com.cmgapps.common.curriculumvitae.CopyRightText
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.cmgapps.common.curriculumvitae.GitHubLink
import com.cmgapps.web.curriculumvitae.AppStyle
import com.cmgapps.web.curriculumvitae.infra.BootstrapVariables
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.value
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement
import org.w3c.dom.get
import org.w3c.dom.set

@Composable
fun PageFooter() {
    Div({
        classes("mt-5", AppStyle.footerContainer)
    },) {
        Footer({
            classes("container", "h-100")
        },) {
            Div({
                classes("row", "h-100", "align-items-center")
            },) {
                Div({
                    classes("col-sm")
                },) {
                    Div({
                        classes("text-center", "text-sm-start")
                    },) {
                        Text(CopyRightText)
                    }
                    Div({
                        classes("text-center", "text-sm-start")
                    },) {
                        Icon("code-slash") {
                            style {
                                property("color", BootstrapVariables.bsPrimary.value())
                            }
                            if (!PRODUCTION) {
                                attr("data-bs-toggle", "modal")
                                attr("data-bs-target", "#BaseUrlModal")
                            }
                        }
                        Text(" with ")
                        Icon("heart-fill") {
                            style {
                                property("color", BootstrapVariables.bsRed.value())
                            }
                        }
                        Text(" and ")
                        A(href = "https://github.com/jetbrains/compose-jb") {
                            Text("Compose for Web")
                        }
                    }
                }
                Div({
                    classes("col-sm", "text-center", "text-sm-end")
                },) {
                    A(href = GitHubLink) {
                        Icon("github") {
                            style {
                                fontSize(32.px)
                            }
                        }
                    }
                }
            }
        }
    }
    if (!PRODUCTION) {
        BaseUrlModal()
    }
}

@Composable
private fun Icon(iconName: String, attrs: AttrBuilderContext<HTMLElement> = {}) {
    I({
        classes("bi-$iconName")
        attrs()
    },)
}

@Composable
private fun BaseUrlModal() {
    val modalLabelId = "BaseUrlModalLabel"
    Div({
        classes("modal", "fade")
        id("BaseUrlModal")
        tabIndex(-1)
        attr("aria-labelledby", modalLabelId)
        attr("aria-hidden", "true")
    },) {
        Div({
            classes("modal-dialog")
        },) {
            Div({
                classes("modal-content")
            },) {
                Div({
                    classes("modal-header")
                },) {
                    H5({
                        classes("modal-title")
                        id(modalLabelId)
                    },) {
                        Text("Base URL")
                    }
                }
                Div({
                    classes("modal-body")
                },) {
                    DebugBaseUrls.mapIndexed { index, url -> "baseUrl.option.$index" to url }
                        .forEach { (key, url) ->
                            RadioButton(key, url)
                        }
                }
                Div({
                    classes("modal-footer")
                },) {
                    Button({
                        type(ButtonType.Button)
                        classes("btn", "btn-secondary")
                        attr("data-bs-dismiss", "modal")
                    },) {
                        Text("Close")
                    }

                    Button({
                        type(ButtonType.Button)
                        classes("btn", "btn-primary")
                        attr("data-bs-dismiss", "modal")
                        onClick {
                            window.location.reload()
                        }
                    },) {
                        Text("Reload")
                    }
                }
            }
        }
    }
}

@Composable
private fun RadioButton(key: String, value: String) {
    Div({
        classes("form-check")
    },) {
        Input(type = InputType.Radio, attrs = {
            classes("form-check-input")
            name("baseUrlDialogRadio")
            id(key)
            onChange { event -> if (event.value) localStorage["baseUrl"] = value }
            checked(localStorage["baseUrl"] == value)
        },)
        Label(key, attrs = {
            classes("form-check-label")
        },) {
            Text(value)
        }
    }
}
