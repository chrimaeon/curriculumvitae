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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.cmgapps.android.curriculumvitae.infra.IconState
import com.cmgapps.android.curriculumvitae.infra.Screen
import com.cmgapps.android.curriculumvitae.infra.SubScreen
import com.cmgapps.android.curriculumvitae.infra.screens
import com.cmgapps.android.curriculumvitae.ui.employment.EmploymentScreen
import com.cmgapps.android.curriculumvitae.ui.employment.detail.EmploymentDetails
import com.cmgapps.android.curriculumvitae.ui.profile.ProfileScreen
import com.cmgapps.android.curriculumvitae.ui.skills.SkillsScreen
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
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
    navController.addOnDestinationChangedListener { _, destination, _ ->
        isOnMainScreen = destination.route?.let { currentRoute ->
            screens.any { it.route == currentRoute }
        } ?: false
    }

    val bottomSheetScaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = BottomSheetState(BottomSheetValue.Collapsed)
    )

    BottomSheetScaffold(
        scaffoldState = bottomSheetScaffoldState,
        sheetContent = {
            BottomSheetContent(onInfoWebsiteLinkClick = onInfoWebsiteLinkClick)
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
            NavHost(navController, startDestination = Screen.Profile.route) {
                composable(Screen.Profile.route) {
                    ProfileScreen(
                        modifier = modifier,
                        viewModel = hiltViewModel(),
                        onEmailClick = onFabClick,
                        bottomContentPadding = FabTopKnobPadding,
                        snackbarHostState = scaffoldState.snackbarHostState
                    )
                }
                navigation(startDestination = Screen.Employment.route, route = "employments") {
                    composable(Screen.Employment.route) {
                        EmploymentScreen(
                            modifier = modifier,
                            bottomContentPadding = FabTopKnobPadding,
                            viewModel = hiltViewModel(),
                            navController = navController,
                            snackbarHostState = scaffoldState.snackbarHostState
                        )
                    }
                    composable(
                        route = SubScreen.EmploymentDetail.route,
                        arguments = SubScreen.EmploymentDetail.arguments
                    ) {
                        EmploymentDetails(
                            modifier = modifier,
                            viewModel = hiltViewModel(),
                            navController = navController
                        )
                    }
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
            val currentRoute = navBackStackEntry?.destination?.route
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
                        if (selected) {
                            return@onClick
                        }

                        navController.navigate(screen.route) {
                            popUpTo(
                                navBackStackEntry?.destination?.id
                                    ?: navController.graph.startDestinationId
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
private fun BottomSheetContent(
    modifier: Modifier = Modifier,
    onInfoWebsiteLinkClick: () -> Unit
) {
    var ossDialogOpen by remember { mutableStateOf(false) }
    var oflDialogOpen by remember { mutableStateOf(false) }
    Column(
        modifier
            .fillMaxWidth()
            .padding(
                rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                    additionalStart = 24.dp,
                    additionalTop = 24.dp,
                    additionalEnd = 24.dp,
                    additionalBottom = 24.dp
                )
            )
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
            url = "licenses.html".asAssetFileUrl(),
            onDismissRequest = { ossDialogOpen = false }
        )
    }
    if (oflDialogOpen) {
        WebViewDialog(
            url = "ofl-licenses.html".asAssetFileUrl(),
            onDismissRequest = { oflDialogOpen = false },
        )
    }
}

private fun String.asAssetFileUrl() = "file:///android_asset/$this"

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
