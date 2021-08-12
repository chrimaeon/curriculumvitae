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
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.web.curriculumvitae.AppStyle
import com.cmgapps.web.curriculumvitae.component.Card
import com.cmgapps.web.curriculumvitae.component.Column
import com.cmgapps.web.curriculumvitae.component.Icon
import com.cmgapps.web.curriculumvitae.component.LoadingBar
import com.cmgapps.web.curriculumvitae.component.Row
import com.cmgapps.web.curriculumvitae.repository.EmploymentRepository
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.keywords.auto
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginLeft
import org.jetbrains.compose.web.css.media
import org.jetbrains.compose.web.css.mediaMaxWidth
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.minWidth
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.plus
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.times
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.H6
import org.jetbrains.compose.web.dom.Text

@Composable
fun EmploymentScreen(repo: EmploymentRepository) {

    val (employments, setEmployments) = remember { mutableStateOf<List<Employment>?>(null) }

    LaunchedEffect(true) {
        try {
            setEmployments(repo.getEmployments())
        } catch (exc: Exception) {
            console.error(exc)
        }
    }

    if (employments == null) {
        LoadingBar()
    } else {
        Content(employments)
    }
}

@Composable
fun Content(employments: List<Employment>) {
    Div(
        attrs = {
            style {
                display(DisplayStyle.Flex)
                justifyContent(JustifyContent.Start)
                alignItems(AlignItems.Center)
                flexDirection(FlexDirection.Column)
                margin(16.px)
            }
        }
    ) {
        employments.forEach {
            Card(EmploymentScreenStyles.card) {
                Row {
                    Div(
                        attrs = {
                            classes("mdl-color--primary")
                            style {
                                display(DisplayStyle.Flex)
                                alignItems(AlignItems.Center)
                                justifyContent(JustifyContent.Center)
                                width(56.px)
                                height(56.px)
                                borderRadius(50.percent)
                            }
                        }
                    ) {
                        Icon("apartment", "mdl-color-text--primary-contrast")
                    }
                    Column(style = {
                        alignItems(AlignItems.FlexStart)
                        marginLeft(8.px)
                    }) {
                        H5(
                            attrs = {
                                style {
                                    margin(16.px, 0.px, 12.px)
                                }
                            }
                        ) {
                            Text(it.employer)
                        }
                        Div(
                            attrs = {
                                classes("mdl-typography--body-1")
                            }
                        ) {
                            val workPeriod = with(it.workPeriod) {
                                buildString {
                                    if (years > 0) {
                                        if (years == 1) {
                                            append("1 Year")
                                        } else {
                                            append("$years Years")
                                        }
                                    }
                                    append(" ")
                                    if (months > 0) {
                                        if (months == 1) {
                                            append("1 Month")
                                        } else {
                                            append("$months Months")
                                        }
                                    }
                                }.trim()
                            }
                            Text(workPeriod)
                        }
                        H6(
                            attrs = {
                                style {
                                    margin(12.px, 0.px, 8.px)
                                }
                            }
                        ) {
                            Text(it.jobTitle)
                        }
                    }
                }
            }
        }
    }
}

object EmploymentScreenStyles : StyleSheet(AppStyle) {
    val card by style {
        val bigWidth = 600.px
        val midWidth = 400.px
        val margin = 8.px
        width(bigWidth)
        padding(16.px)
        margin(margin)
        minHeight("auto")

        media(mediaMaxWidth(bigWidth + (margin * 2))) {
            self style {
                width(midWidth)
            }
        }

        media(mediaMaxWidth(midWidth + (margin * 2))) {
            self style {
                width(auto)
                minWidth(320.px)
            }
        }
    }
}
