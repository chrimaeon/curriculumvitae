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

import android.annotation.SuppressLint
import android.net.Uri
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.plusAssign
import com.cmgapps.android.curriculumvitae.infra.DecorativeImage
import com.cmgapps.android.curriculumvitae.infra.IconState
import com.cmgapps.android.curriculumvitae.infra.Screen
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.screens
import com.cmgapps.android.curriculumvitae.ui.employment.EmploymentScreen
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetails
import com.cmgapps.android.curriculumvitae.ui.info.InfoSheet
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.skills.SkillsScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.google.accompanist.navigation.material.rememberBottomSheetNavigator

@OptIn(
    ExperimentalMaterialNavigationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onFabClick: () -> Unit = {},
    onOpenWebsite: (Uri) -> Unit = {}
) {
    val navController = rememberAnimatedNavController()
    val bottomSheetNavigator = rememberBottomSheetNavigator()
    @SuppressLint("RestrictedApi")
    navController.navigatorProvider += bottomSheetNavigator

    var isOnMainScreen by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, destination, _ ->
        isOnMainScreen = destination.route?.let { currentRoute ->
            screens.any { it.route == currentRoute }
        } ?: false
    }

    ModalBottomSheetLayout(bottomSheetNavigator) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = { if (isOnMainScreen) Fab(onFabClick) },
            isFloatingActionButtonDocked = true,
            bottomBar = { if (isOnMainScreen) BottomBar(navController = navController) }
        ) { innerPadding ->
            MainScreenNavHost(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                scaffoldState = scaffoldState,
                onFabClick = onFabClick,
                onOpenWebsite = onOpenWebsite,
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun MainScreenNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    scaffoldState: ScaffoldState,
    onFabClick: () -> Unit,
    onOpenWebsite: (Uri) -> Unit
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
                        slideInHorizontally(initialOffsetX = { -it })
                    } else {
                        enterTransition()
                    }
                },
                exitTransition = { _, target ->
                    if (target.destination.route == SubScreen.EmploymentDetail.route) {
                        slideOutHorizontally(targetOffsetX = { -it })
                    } else {
                        exitTransition()
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
                enterTransition = { _, _ -> slideInHorizontally(initialOffsetX = { it }) },
                exitTransition = { _, _ -> slideOutHorizontally(targetOffsetX = { it }) },
            ) { EmploymentDetails(viewModel = hiltViewModel()) { navController.popBackStack() } }
        }
        composable(
            route = Screen.Skills.route,
            enterTransition = defaultEnterTransition,
            exitTransition = defaultExitTransition,
        ) { SkillsScreen() }

        bottomSheet(Screen.Info.route) {
            InfoSheet(onOpenWebsite = onOpenWebsite)
        }
    }
}

private val FabTopKnobPadding = 40.dp

@OptIn(ExperimentalAnimationApi::class)
typealias EnterTransitionFunction = AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> EnterTransition

@OptIn(ExperimentalAnimationApi::class)
typealias ExitTransitionFunction = AnimatedContentScope<String>.(NavBackStackEntry, NavBackStackEntry) -> ExitTransition

@OptIn(ExperimentalAnimationApi::class)
private val defaultEnterTransition: EnterTransitionFunction = { _, _ -> enterTransition() }

@OptIn(ExperimentalAnimationApi::class)
private val defaultExitTransition: ExitTransitionFunction = { _, _ -> exitTransition() }

private const val DefaultTransitionDuration = 150

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

@Composable
private fun BottomBar(navController: NavController) {
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

            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            screens.forEach { screen ->
                NavigationItem(
                    screen = screen,
                    navController,
                    currentDestination
                )
            }
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

@Composable
private fun RowScope.NavigationItem(
    screen: Screen,
    navController: NavController,
    currentDestination: NavDestination?,
) {
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
                if (screen.route != Screen.Info.route) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }

                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    )
}
