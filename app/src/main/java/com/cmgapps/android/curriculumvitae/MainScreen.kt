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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltNavGraphViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.cmgapps.android.curriculumvitae.components.ContentError
import com.cmgapps.android.curriculumvitae.infra.IconState
import com.cmgapps.android.curriculumvitae.infra.NavArguments
import com.cmgapps.android.curriculumvitae.infra.Screen
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.screens
import com.cmgapps.android.curriculumvitae.ui.employment.EmploymentScreen
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetails
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.skills.SkillsScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.toPaddingValues

@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onFabClick: () -> Unit = {}
) {

    val navController = rememberNavController()
    var isOnMainScreen by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, _, arguments ->
        isOnMainScreen = arguments?.getString(KEY_ROUTE).let { currentRoute ->
            screens.any { it.route == currentRoute }
        }
    }

    Scaffold(
        scaffoldState = scaffoldState,
        floatingActionButton = { if (isOnMainScreen) Fab(onFabClick) },
        isFloatingActionButtonDocked = true,
        bottomBar = { if (isOnMainScreen) BottomBar(navController = navController) }
    ) { innerPadding ->
        val modifier = Modifier.padding(innerPadding)

        NavHost(navController, startDestination = Screen.Profile.route) {
            composable(Screen.Profile.route) {
                ProfileScreen(
                    modifier = modifier,
                    viewModel = hiltNavGraphViewModel(),
                    onEmailClick = onFabClick,
                    bottomContentPadding = FabTopKnobPadding
                )
            }
            composable(Screen.Employment.route) {
                EmploymentScreen(
                    modifier = modifier,
                    bottomContentPadding = FabTopKnobPadding,
                    viewModel = hiltNavGraphViewModel(),
                    navController = navController,
                )
            }
            composable(
                route = SubScreen.EmploymentDetail.route,
                arguments = SubScreen.EmploymentDetail.arguments
            ) {
                it.arguments?.getInt(NavArguments.EMPLOYMENT_ID.argumentName)?.let {
                    EmploymentDetails(
                        modifier = modifier,
                        employmentId = it,
                        viewModel = hiltNavGraphViewModel(),
                        navController = navController
                    )
                } ?: ContentError(error = IllegalStateException("employment id not set"))
            }
            composable(Screen.Skills.route) {
                SkillsScreen()
            }
        }
    }
}

val FabTopKnobPadding = 40.dp

@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = BottomNavigationDefaults.Elevation,
        contentColor = MaterialTheme.colors.primary,
        contentPadding = LocalWindowInsets.current.navigationBars.toPaddingValues(
            additionalEnd = (56 + 16).dp
        ),
        cutoutShape = CircleShape
    ) {
        BottomNavigation(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
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

@Composable
fun Fab(onClick: () -> Unit = {}) {
    FloatingActionButton(
        modifier = Modifier.testTag("Fab"),
        onClick = onClick
    ) {
        Icon(
            imageVector = Icons.Default.Send,
            contentDescription = stringResource(id = R.string.send_action)
        )
    }
}
