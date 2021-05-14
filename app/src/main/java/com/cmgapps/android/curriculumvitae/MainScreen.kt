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

import android.webkit.WebView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationDefaults
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.ScaffoldState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainScreen(
    scaffoldState: ScaffoldState = rememberScaffoldState(),
    onFabClick: () -> Unit = {},
    onInfoWebsiteLinkClick: () -> Unit = {}
) {

    val navController = rememberNavController()
    var isOnMainScreen by remember { mutableStateOf(true) }
    navController.addOnDestinationChangedListener { _, _, arguments ->
        isOnMainScreen = arguments?.getString(KEY_ROUTE).let { currentRoute ->
            screens.any { it.route == currentRoute }
        }
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(onInfoWebsiteLinkClick)
        },
        sheetPeekHeight = 0.dp
    ) {
        Scaffold(
            scaffoldState = scaffoldState,
            floatingActionButton = { if (isOnMainScreen) Fab(onFabClick) },
            isFloatingActionButtonDocked = true,
            bottomBar = {
                if (isOnMainScreen) BottomBar(
                    navController = navController,
                    bottomSheetState = bottomSheetScaffoldState.bottomSheetState
                )
            }
        ) { innerPadding ->
            val modifier = Modifier.padding(innerPadding)

            val onError: @Composable (String) -> Unit = { message ->
                LaunchedEffect(scaffoldState) {
                    scaffoldState.snackbarHostState.showSnackbar(message)
                }
            }

            NavHost(navController, startDestination = Screen.Profile.route) {
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        modifier = modifier,
                        viewModel = hiltNavGraphViewModel(),
                        onEmailClick = onFabClick,
                        bottomContentPadding = FabTopKnobPadding,
                        onError = onError
                    )
                }
                composable(Screen.Employment.route) {
                    EmploymentScreen(
                        modifier = modifier,
                        bottomContentPadding = FabTopKnobPadding,
                        viewModel = hiltNavGraphViewModel(),
                        navController = navController,
                        onError = onError
                    )
                }
                composable(
                    route = SubScreen.EmploymentDetail.route,
                    arguments = SubScreen.EmploymentDetail.arguments
                ) { entry ->
                    entry.arguments?.getInt(NavArguments.EMPLOYMENT_ID.argumentName)?.let {
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
}

val FabTopKnobPadding = 40.dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun BottomBar(navController: NavController, bottomSheetState: BottomSheetState) {
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

            val coroutineScope = rememberCoroutineScope()
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
                            text = stringResource(id = screen.labelRes),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
            BottomNavigationItem(
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null
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
                        bottomSheetState.expand()
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

@Composable
private fun BottomSheetContent(onInfoWebsiteLinkClick: () -> Unit) {
    var ossDialogOpen by remember { mutableStateOf(false) }
    var oflDialogOpen by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h5,
        )
        Text(
            text = stringResource(
                id = R.string.version,
                BuildConfig.VERSION_NAME,
                BuildConfig.VERSION_CODE
            )
        )
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(id = R.string.info_copyright, BuildConfig.BUILD_YEAR)
        )
        InfoTextWithLink(
            text = stringResource(id = R.string.info_cmgapps_link),
            onClick = { onInfoWebsiteLinkClick() }
        )
        InfoTextWithLink(
            text = stringResource(id = R.string.info_oss_licenses),
            onClick = { ossDialogOpen = true }
        )
        InfoTextWithLink(
            text = stringResource(id = R.string.info_open_font_licenses),
            onClick = { oflDialogOpen = true }
        )
    }

    if (ossDialogOpen) {
        WebViewDialog(
            url = "file:///android_asset/licenses.html",
            onDismissRequest = { ossDialogOpen = false }
        )
    }
    if (oflDialogOpen) {
        WebViewDialog(
            url = "file:///android_asset/ofl-licenses.html",
            onDismissRequest = { oflDialogOpen = false },
        )
    }
}

@Composable
private fun InfoTextWithLink(
    text: String,
    onClick: () -> Unit,
) {
    Text(
        color = MaterialTheme.colors.primaryVariant,
        modifier = Modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(color = MaterialTheme.colors.primaryVariant),
                onClick = onClick,
                role = Role.Button
            )
            .padding(vertical = 8.dp),
        text = text
    )
}

@Composable
private fun WebViewDialog(
    url: String,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Surface(Modifier.fillMaxSize()) {
            AndroidView(factory = ::WebView) { webView ->
                webView.loadUrl(url)
            }
        }
    }
}
