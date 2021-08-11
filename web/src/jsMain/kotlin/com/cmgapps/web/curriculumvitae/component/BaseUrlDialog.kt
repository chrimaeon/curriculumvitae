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

package com.cmgapps.web.curriculumvitae.component

import androidx.compose.runtime.Composable
import com.cmgapps.web.curriculumvitae.debugBaseUrls
import kotlinx.browser.localStorage
import kotlinx.browser.window
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.checked
import org.jetbrains.compose.web.attributes.forId
import org.jetbrains.compose.web.attributes.name
import org.jetbrains.compose.web.attributes.type
import org.jetbrains.compose.web.attributes.value
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Input
import org.jetbrains.compose.web.dom.Label
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.TagElement
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLDialogElement
import org.w3c.dom.get

@Composable
fun BaseUrlDialog(id: String, onDialogRef: (HTMLDialogElement) -> Unit) {
    TagElement<HTMLDialogElement>("dialog", {
        id(id)
        classes("mdl-dialog")
    }) {
        var dialog: HTMLDialogElement? = null
        DomSideEffect {
            dialog = it
            onDialogRef(it)
        }

        H4(attrs = { classes("mdl-dialog__title") }) {
            Text("Change base url")
        }

        Div(attrs = {
            classes("mdl-dialog__content")
        }) {
            debugBaseUrls.mapIndexed { index, value -> "baseUrl.option.$index" to value }
                .forEach { (key, value) ->
                    Label(attrs = {
                        classes("mdl-radio", "mdl-js-radio", "mdl-js-ripple-effect")
                        forId(key)
                    }) {
                        Input(type = InputType.Radio, attrs = {
                            classes("mdl-radio__button")
                            id(key)
                            name("baseUrlOptions")
                            value(value)
                            if (localStorage["baseUrl"] == value) {
                                checked()
                            }
                            onChange { event ->
                                if (event.value) {
                                    window.localStorage.setItem("baseUrl", value)
                                }
                            }
                        })
                        Span(attrs = {
                            classes("mdl-radio__label")
                        }) {
                            Text(value)
                        }
                    }
                }
        }

        Div(attrs = {
            classes("mdl-dialog__actions")
        }) {
            Button(attrs = {
                classes("mdl-button")
                type(ButtonType.Button)
                onClick {
                    dialog?.close()
                    window.location.reload()
                }
            }) {
                Text("Done")
            }
        }
    }
}
