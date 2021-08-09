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
import org.jetbrains.compose.web.attributes.href
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Nav
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text

@Composable
fun Header(title: String, setRoute: (Route) -> Unit) {

    org.jetbrains.compose.web.dom.Header(attrs = { classes("mdl-layout__header") }) {
        Div(attrs = {
            classes("mdl-layout__header-row")
        }) {
            Span(attrs = { classes("mdl-layout-title") }) {
                Text(title)
            }
            Div(attrs = {
                classes("mdl-layout-spacer")
            })
            Nav(attrs = {
                classes("mdl-navigation", "mdl-layout--large-screen-only")
            }) {
                A(attrs = {
                    onClick { setRoute(Route.Profile) }
                    classes("mdl-navigation__link")
                    href("#")
                }) {
                    Text(Route.Profile.title)
                }
                A(attrs = {
                    onClick { setRoute(Route.Employment) }
                    classes("mdl-navigation__link")
                    href("#")
                }) {
                    Text(Route.Employment.title)
                }
            }
        }
    }
}
