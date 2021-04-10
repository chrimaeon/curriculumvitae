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

package com.cmgapps.android.curriculumvitae.infra

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.ui.graphics.vector.ImageVector
import com.cmgapps.android.curriculumvitae.R

sealed class IconState {
    object Selected : IconState()
    object Default : IconState()
}

class StateIcon(
    private val defaultIcon: ImageVector,
    private val selectedIcon: ImageVector = defaultIcon,
) {
    operator fun get(state: IconState): ImageVector = when (state) {
        is IconState.Selected -> selectedIcon
        is IconState.Default -> defaultIcon
    }
}

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: StateIcon,
) {
    object Profile : Screen(
        "profile",
        R.string.profile_label,
        StateIcon(
            defaultIcon = Icons.Outlined.AccountCircle,
            selectedIcon = Icons.Filled.AccountCircle
        )
    )

    object Employment : Screen(
        "employment",
        R.string.employment_label,
        StateIcon(
            defaultIcon = Icons.Outlined.WorkOutline,
            selectedIcon = Icons.Filled.Work
        )
    )

    object Skills : Screen(
        "skills",
        R.string.skills_label,
        StateIcon(
            defaultIcon = Icons.Outlined.BuildCircle,
            selectedIcon = Icons.Filled.BuildCircle
        )
    )
}

val screens = listOf(
    Screen.Profile,
    Screen.Employment,
    Screen.Skills
)
