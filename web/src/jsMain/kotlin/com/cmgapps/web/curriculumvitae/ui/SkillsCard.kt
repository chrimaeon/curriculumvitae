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
import com.cmgapps.common.curriculumvitae.data.domain.Skill
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.cmgapps.web.curriculumvitae.AppStyle
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexWrap
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.em
import org.jetbrains.compose.web.css.flexWrap
import org.jetbrains.compose.web.css.fontSize
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.lineHeight
import org.jetbrains.compose.web.css.listStyle
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.plus
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.times
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Ul

@Composable
fun SkillsCard(repository: SkillsRepository) {
    val (skills, setSkills) = remember { mutableStateOf<List<Skill>?>(null) }

    LaunchedEffect(true) {
        setSkills(repository.getSkills())
    }

    if (skills != null && skills.isNotEmpty()) {
        Div({
            classes("row")
        }) {
            Div({
                classes("col")
            }) {
                Div({
                    classes("card", "mt-3", "align-items-center")
                }) {
                    Ul({
                        classes(SkillsStyle.cloud)
                    }) {
                        skills.forEach {
                            Li({
                                style {
                                    fontSize(0.5.em * it.level + 0.75.em)
                                    padding(0.125.cssRem, 0.25.cssRem)
                                }
                            }) {
                                Text(it.name)
                            }
                        }
                    }
                }
            }
        }
    }
}

object SkillsStyle : StyleSheet(AppStyle) {
    val cloud by style {
        listStyle("none")
        padding(1.em)
        display(DisplayStyle.Flex)
        flexWrap(FlexWrap.Wrap)
        alignItems(AlignItems.Center)
        justifyContent(JustifyContent.Center)
        lineHeight(2.5f.cssRem)
        width(50.percent)
        margin(0.px)
    }

    init {
        "$cloud li"
    }
}
