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

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navigation
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.infra.IconState
import com.cmgapps.android.curriculumvitae.infra.Screen
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.screens
import com.cmgapps.android.curriculumvitae.ui.InfoBottomSheet
import com.cmgapps.android.curriculumvitae.ui.employment.EmploymentScreen
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetails
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.skills.SkillsScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class, ExperimentalAnimationApi::class)
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onFabClick: () -> Unit = {},
    onOpenWebsite: (Uri) -> Unit = {}
) {

    val navController = rememberAnimatedNavController()
    var isOnMainScreen by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        isOnMainScreen = destination.route?.let { currentRoute ->
            screens.any { it.route == currentRoute }
        } ?: false
    }

    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)

    ModalBottomSheetLayout(
        sheetState = bottomSheetState,
        sheetContent = {
            InfoBottomSheet(onOpenWebsite)
        },
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = { if (isOnMainScreen) Fab(onFabClick) },
            isFloatingActionButtonDocked = true,
            bottomBar = {
                if (isOnMainScreen) BottomBar(
                    navController = navController,
                    bottomSheetState = bottomSheetState
                )
            }
        ) { innerPadding ->
            MainScreenNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                scaffoldState = scaffoldState,
                onFabClick = onFabClick,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MainScreenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    onFabClick: () -> Unit,
) {
    AnimatedNavHost(navController, startDestination = Screen.Profile.route) {
        composable(
            route = Screen.Profile.route,
            enterTransition = defaultEnterTransition,
            exitTransition = defaultExitTransition,
        ) {
            ProfileScreen(
                modifier = modifier,
                viewModel = hiltViewModel(),
                onEmailClick = onFabClick,
                bottomContentPadding = FabTopKnobPadding,
                snackbarHostState = scaffoldState.snackbarHostState
            )
        }
        navigation(startDestination = Screen.Employment.route, route = "employments") {
            composable(
                Screen.Employment.route,
                enterTransition = { initial, _ ->
                    if (initial.destination.route == SubScreen.EmploymentDetail.route) {
                        slideInHorizontally({ -it })
                    } else {
                        enterTransition()
                    }
                },
                exitTransition = { _, target ->
                    if (target.destination.route == SubScreen.EmploymentDetail.route) {
                        slideOutHorizontally({ -it })
                    } else {
                        fadeOut(
                            animationSpec = tween(
                                durationMillis = DefaultTransitionDuration
                            )
                        )
                    }
                }
            ) {
                EmploymentScreen(
                    modifier = modifier,
                    bottomContentPadding = FabTopKnobPadding,
                    viewModel = hiltViewModel(),
                    snackbarHostState = scaffoldState.snackbarHostState
                ) { id -> navController.navigate(SubScreen.EmploymentDetail.routeWithId(id)) }
            }
            composable(
                route = SubScreen.EmploymentDetail.route,
                arguments = SubScreen.EmploymentDetail.arguments,
                enterTransition = { _, _ -> slideInHorizontally({ it }) },
                exitTransition = { _, _ -> slideOutHorizontally({ it }) },
            ) { EmploymentDetails(viewModel = hiltViewModel()) { navController.popBackStack() } }
        }
        composable(
            route = Screen.Skills.route,
            enterTransition = defaultEnterTransition,
            exitTransition = defaultExitTransition,
        ) { SkillsScreen() }
    }
}

private val FabTopKnobPadding = 40.dp
private const val DefaultTransitionDuration = 150

private val defaultEnterTransition = { _: NavBackStackEntry, _: NavBackStackEntry ->
    enterTransition()
}

private val defaultExitTransition = { _: NavBackStackEntry, _: NavBackStackEntry ->
    exitTransition()
}

@OptIn(ExperimentalAnimationApi::class)
private fun enterTransition() = fadeIn(
    animationSpec = tween(
        durationMillis = DefaultTransitionDuration
    )
)

@OptIn(ExperimentalAnimationApi::class)
private fun exitTransition() = fadeOut(
    animationSpec = tween(
        durationMillis = DefaultTransitionDuration
    )
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomBar(navController: NavController, bottomSheetState: ModalBottomSheetState) {
    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface,
        elevation = BottomNavigationDefaults.Elevation,
        contentColor = MaterialTheme.colors.primary,
        contentPadding = rememberInsetsPaddingValues(
            insets = LocalWindowInsets.current.navigationBars,
            additionalEnd = (56 + 16).dp
        ),
        cutoutShape = CircleShape
    ) {
        BottomNavigation(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
        ) {

            val coroutineScope = rememberCoroutineScope()
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screens.forEach { screen ->
                val selected =
                    currentDestination?.hierarchy?.any { it.route == screen.route } == true
                val iconState = if (selected) IconState.Selected else IconState.Default

                BottomNavigationItem(
                    icon = {
                        Icon(
                            imageVector = screen.icon[iconState],
                            contentDescription = DecorativeImage
                        )
                    },
                    label = {
                        Text(
                            fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
                            text = stringResource(id = screen.labelRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    selected = selected,
                    onClick = onClick@{
                        if (selected) {
                            return@onClick
                        }

                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                                inclusive = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = DecorativeImage
                    )
                },
                label = {
                    Text(
                        text = stringResource(id = R.string.info)
                    )
                },
                selected = false,
                onClick = {
                    coroutineScope.launch {
                        bottomSheetState.show()
                    }
                }
            )
        }
    }
}

@Composable
private fun Fab(onClick: () -> Unit = {}) {
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
