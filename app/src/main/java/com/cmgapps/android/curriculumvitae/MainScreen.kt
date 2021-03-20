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

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.cmgapps.android.curriculumvitae.infra.IconState
import com.cmgapps.android.curriculumvitae.infra.Screen
import com.cmgapps.android.curriculumvitae.infra.screens
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.skills.SkillsScreen
import com.cmgapps.android.curriculumvitae.ui.work.WorkScreen
import dev.chrisbanes.accompanist.insets.LocalWindowInsets

@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onFabClick: () -> Unit = {}
) {
    val navController = rememberNavController()
    val insets = LocalWindowInsets.current

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = { Fab(onFabClick) },
        bottomBar = {
            Surface(
                color = MaterialTheme.colors.background
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
                        val iconState = if (selected) IconState.Selected else IconState.Default

                        BottomNavigationItem(
                            icon = {
                                Icon(
                                    imageVector = screen.icon[iconState],
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
                            onClick = onClick@{
                                if (screen.route == currentRoute) {
                                    return@onClick
                                }

                                navController.navigate(screen.route) {
                                    popUpTo(
                                        navBackStackEntry?.destination?.id
                                            ?: navController.graph.startDestination
                                    ) {
                                        inclusive = true
                                    }
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
            composable(Screen.Profile.route) { ProfileScreen() }
            composable(Screen.Work.route) { WorkScreen() }
            composable(Screen.Skills.route) { SkillsScreen() }
        }
    }
}

@Composable
fun Fab(onClick: () -> Unit = {}) {
    FloatingActionButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = stringResource(id = R.string.send_action)
        )
    }
}
