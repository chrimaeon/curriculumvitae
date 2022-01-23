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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cmgapps.common.curriculumvitae.data.domain.Profile
import com.cmgapps.common.curriculumvitae.infra.di.provideBaseUrl
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.web.curriculumvitae.AppStyle
import org.jetbrains.compose.web.css.CSSVariableValue
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.backgroundImage
import org.jetbrains.compose.web.css.backgroundPosition
import org.jetbrains.compose.web.css.backgroundRepeat
import org.jetbrains.compose.web.css.backgroundSize
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H3
import org.jetbrains.compose.web.dom.H6
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfileCard(repository: ProfileRepository) {
    val (profile, setProfile) = remember { mutableStateOf<Profile?>(null) }

    LaunchedEffect(true) {
        setProfile(repository.getProfile())
    }

    if (profile != null) {
        Div({
            classes("row")
        }) {
            Div({
                classes("col")
            }) {
                Div({
                    classes("card")
                }) {
                    Div({
                        classes("card-body")
                    }) {
                        Div({
                            classes("container")
                        }) {
                            Div({
                                classes(
                                    "row",
                                    "justify-content-center",
                                    "align-items-md-center",
                                    "justify-content-md-start"
                                )
                            }) {
                                Div({
                                    style {
                                        backgroundImage("url('${provideBaseUrl()}${profile.profileImagePath}')")
                                    }
                                    classes("col-12", "col-md-4", ProfileStyle.profileImage)
                                })
                                Div({
                                    classes("col-12", "col-md-8")
                                }) {
                                    H1 {
                                        Text(profile.name)
                                    }
                                    H3 {
                                        Text(profile.address.street)
                                    }
                                    H3 {
                                        Text("${profile.address.postalCode} ${profile.address.city}")
                                    }
                                    A(href = "mailto:${profile.email}") {
                                        H6 {
                                            Text(profile.email)
                                        }
                                    }
                                    A(href = "tel:${profile.phone}") {
                                        H6 {
                                            Text(profile.phone)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private object ProfileStyle : StyleSheet(AppStyle) {
    val profileImage by style {
        width(200.px)
        height(200.px)
        backgroundRepeat("no-repeat")
        backgroundSize("cover")
        backgroundPosition("center")
        borderRadius(50.percent)
        property("background-color", CSSVariableValue<String>("bs-gray-500"))
    }
}
