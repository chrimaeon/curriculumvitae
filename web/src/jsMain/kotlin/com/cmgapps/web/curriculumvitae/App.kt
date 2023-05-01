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

package com.cmgapps.web.curriculumvitae

import androidx.compose.runtime.Composable
import com.cmgapps.web.curriculumvitae.infra.BootstrapVariables
import com.cmgapps.web.curriculumvitae.ui.Employments
import com.cmgapps.web.curriculumvitae.ui.OssProjects
import com.cmgapps.web.curriculumvitae.ui.PageFooter
import com.cmgapps.web.curriculumvitae.ui.ProfileCard
import com.cmgapps.web.curriculumvitae.ui.SkillsCard
import org.jetbrains.compose.web.css.AlignItems
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.JustifyContent
import org.jetbrains.compose.web.css.Position
import org.jetbrains.compose.web.css.Style
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.alignItems
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.bottom
import org.jetbrains.compose.web.css.cssRem
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.div
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.justifyContent
import org.jetbrains.compose.web.css.left
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.marginBottom
import org.jetbrains.compose.web.css.marginRight
import org.jetbrains.compose.web.css.marginTop
import org.jetbrains.compose.web.css.minHeight
import org.jetbrains.compose.web.css.minus
import org.jetbrains.compose.web.css.padding
import org.jetbrains.compose.web.css.paddingBottom
import org.jetbrains.compose.web.css.plus
import org.jetbrains.compose.web.css.position
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.right
import org.jetbrains.compose.web.css.textDecoration
import org.jetbrains.compose.web.css.value
import org.jetbrains.compose.web.css.vh
import org.jetbrains.compose.web.dom.Div
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

@Composable
fun App(koin: Koin) {
    Style(AppStyle)
    Div(
        {
            classes("container")
            style { marginTop(AppStyle.bodyPadding) }
        },
    ) {
        ProfileCard(koin.get())
        Employments(koin.get())
        SkillsCard(koin.get())
        OssProjects(koin.get(), koin.get { parametersOf("main") })
    }
    PageFooter()
}

object AppStyle : StyleSheet() {
    private val footerHeight = 100.px
    val bodyPadding = 230.px

    val footerContainer by style {
        property("background-color", BootstrapVariables.bsGray900.value())
        position(Position.Absolute)
        bottom(0.px)
        left(0.px)
        right(0.px)
        height(footerHeight)
    }

    init {
        id("root") style {
            position(Position.Relative)
            margin(0.px)
            paddingBottom(footerHeight + 4.cssRem)
            minHeight(100.vh - bodyPadding)
        }
        "footer, footer a" style {
            property("color", BootstrapVariables.bsGray100.value())
            textDecoration("none")
        }
        "a" style {
            property("color", BootstrapVariables.bsPrimary.value())
        }
    }

    val starChip by style {
        property("background-color", BootstrapVariables.bsPrimary.value())
        val height = 30.px
        borderRadius(height / 2)
        height(height)
        alignItems(AlignItems.Center)
        display(DisplayStyle.LegacyInlineFlex)
        justifyContent(JustifyContent.Center)
        padding(0.25.cssRem, 0.5.cssRem)

        desc(self, "img") style {
            marginRight(0.25.cssRem)
        }
    }

    val ossProjectHeaderContainer by style {
        display(DisplayStyle.Flex)
        justifyContent(JustifyContent.SpaceBetween)
        alignItems(AlignItems.Center)
        marginBottom(0.5.cssRem)

        desc(self, "h4") style {
            margin(0.px)
        }
    }
}
