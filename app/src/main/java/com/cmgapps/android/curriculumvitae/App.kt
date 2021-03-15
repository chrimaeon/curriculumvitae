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

package com.cmgapps.android.curriculumvitae

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.BuildCircle
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.BuildCircle
import androidx.compose.material.icons.outlined.WorkOutline
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.cmgapps.android.compomaeon.ui.Theme
import dev.chrisbanes.accompanist.insets.LocalWindowInsets

sealed class Screen(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
    val selectedIcon: ImageVector
) {
    object Profile : Screen(
        "profile",
        R.string.profile_label,
        Icons.Outlined.AccountCircle,
        Icons.Filled.AccountCircle
    )

    object Work : Screen(
        "work",
        R.string.work_label,
        Icons.Outlined.WorkOutline,
        Icons.Filled.Work
    )

    object Skills : Screen(
        "skills",
        R.string.skills_label,
        Icons.Outlined.BuildCircle,
        Icons.Filled.BuildCircle
    )
}

private val screens = listOf(
    Screen.Profile,
    Screen.Work,
    Screen.Skills
)

@Composable
fun App() {
    val navController = rememberNavController()
    val insets = LocalWindowInsets.current

    Scaffold(
        bottomBar = {
            Box(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
            ) {
                BottomNavigation(
                    modifier = Modifier.padding(bottom = with(LocalDensity.current) { insets.navigationBars.bottom.toDp() }),
                    elevation = 0.dp,
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)
                    screens.forEach { screen ->
                        val selected = currentRoute == screen.route

                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = if (selected) screen.selectedIcon else screen.icon,
                                    contentDescription = null
                                )
                            },
                            label = {
                                Text(
                                    fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                                    text = stringResource(id = screen.labelRes)
                                )
                            },
                            selected = selected,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo = navController.graph.startDestination
                                    launchSingleTop = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        NavHost(navController, startDestination = Screen.Profile.route) {
            composable(Screen.Profile.route) { Tab(title = "Profile") }
            composable(Screen.Work.route) { Tab(title = "Work") }
            composable(Screen.Skills.route) { Tab(title = "Skills") }
        }
    }
}

@Composable
fun Tab(
    title: String,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h1,
            text = title
        )
    }
}

@Preview("App Dark", widthDp = 360, heightDp = 640)
@Composable
fun DarkApp() {
    Theme(darkTheme = true) {
        App()
    }
}

@Preview("App Light", widthDp = 360, heightDp = 640)
@Composable
fun LightApp() {
    Theme(darkTheme = false) {
        App()
    }
}
