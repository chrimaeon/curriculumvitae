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
import com.cmgapps.web.curriculumvitae.AppStyle
import com.cmgapps.web.curriculumvitae.component.Column
import com.cmgapps.web.curriculumvitae.component.LoadingBar
import com.cmgapps.web.curriculumvitae.repository.ProfileRepository
import org.jetbrains.compose.web.css.StyleSheet
import org.jetbrains.compose.web.css.background
import org.jetbrains.compose.web.css.borderRadius
import org.jetbrains.compose.web.css.fontFamily
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.maxWidth
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.textAlign
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.A
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.P
import org.jetbrains.compose.web.dom.Text

@Composable
fun ProfileScreen(repo: ProfileRepository) {
    val (profile, setProfile) = remember { mutableStateOf<Profile?>(null) }

    LaunchedEffect(true) {
        try {
            setProfile(repo.getProfile())
        } catch (exc: Exception) {
            console.error(exc)
        }
    }

    if (profile == null) {
        LoadingBar()
    } else {
        (Content(profile))
    }
}

@Composable
private fun Content(profile: Profile) {
    Column(
        style = {
            margin(16.px)
        }
    ) {
        Div(attrs = {
            style {
                borderRadius(50.percent)
                width(200.px)
                height(200.px)
                background("""no-repeat center/cover url("${profile.profileImageUrl}")""")
            }
        })
        H1(attrs = {
            style {
                fontFamily("MonteCarlo", "cursive")
            }
        }) {
            Text(profile.name)
        }
        H4(attrs = { classes(ProfileStyles.h4) }) {
            Text(profile.address.street)
        }
        H4(attrs = { classes(ProfileStyles.h4) }) {
            Text("${profile.address.postalCode} ${profile.address.city}")
        }
        A(href = "mailto:${profile.email}") {
            H4(attrs = { classes(ProfileStyles.h4) }) {
                Text(profile.email)
            }
        }
        A(href = "tel:${profile.phone}") {
            H4(attrs = { classes(ProfileStyles.h4) }) {
                Text(profile.phone)
            }
        }
        Div(attrs = {
            style {
                height(24.px)
            }
        })
        profile.intro.forEach {
            P(attrs = { classes(ProfileStyles.intro) }) {
                Text(it)
            }
        }
    }
}

object ProfileStyles : StyleSheet(AppStyle) {
    val h4 by style {
        margin(16.px, 0.px, 8.px)
    }

    val intro by style {
        maxWidth(600.px)
        textAlign("center")
    }
}
