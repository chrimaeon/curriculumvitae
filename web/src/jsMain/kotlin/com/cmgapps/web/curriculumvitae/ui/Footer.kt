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

package com.cmgapps.web.curriculumvitae.ui

import androidx.compose.runtime.Composable
import com.cmgapps.web.curriculumvitae.AppStyle
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.rgb
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.AttrBuilderContext
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Footer
import org.jetbrains.compose.web.dom.I
import org.jetbrains.compose.web.dom.Text
import org.w3c.dom.HTMLElement

@Composable
fun PageFooter() {
    Div({
        classes("mt-5", AppStyle.footerContainer)
    }) {
        Footer({
            classes("container", "h-100")
        }) {
            Div({
                classes("row", "h-100", "align-items-center")
            }) {
                Div({
                    classes("col-sm", "text-center", "text-sm-start")
                }) {
                    Icon("code-slash") {
                        style {
                            color(rgb(53, 150, 232))
                        }
                    }
                    Text(" with ")
                    Icon("heart-fill") {
                        style {
                            color(rgb(221, 0, 18))
                        }
                    }
                    Text(" and ")
                    A(href = "https://github.com/jetbrains/compose-jb") {
                        Text("Compose for Web")
                    }
                }
                Div({
                    classes("col-sm", "text-center", "text-sm-end")
                }) {
                    A(href = "https://github.com/chrimaeon/curriculumvitae") {
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
}

@Composable
private fun Icon(iconName: String, attrs: AttrBuilderContext<HTMLElement> = {}) {
    I({
        classes("bi-$iconName")
        attrs()
    })
}
