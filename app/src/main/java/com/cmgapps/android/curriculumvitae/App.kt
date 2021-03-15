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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.cmgapps.android.compomaeon.ui.Theme

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController, startDestination = "profile") {
        composable("profile") {
            Template(title = "Profile",
                firstButtonTitle = "to Work",
                onFirstButtonClick = {
                    navController.navigate("work")
                },
                secondButtonTitle = "to Skills",
                onSecondButtonClick = {
                    navController.navigate("skills")
                }

            )
        }

        composable("work") {
            Template(title = "Work",
                firstButtonTitle = "to Profile",
                onFirstButtonClick = {
                    navController.navigate("profile")
                },
                secondButtonTitle = "to Skills",
                onSecondButtonClick = {
                    navController.navigate("skills")
                }

            )
        }

        composable("skills") {
            Template(title = "Skills",
                firstButtonTitle = "to Profile",
                onFirstButtonClick = {
                    navController.navigate("profile")
                },
                secondButtonTitle = "to work",
                onSecondButtonClick = {
                    navController.navigate("work")
                }
            )
        }
    }
}

@Composable
fun Template(
    title: String,
    firstButtonTitle: String,
    onFirstButtonClick: () -> Unit,
    secondButtonTitle: String,
    onSecondButtonClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            color = MaterialTheme.colors.onBackground,
            style = MaterialTheme.typography.h1,
            text = title
        )
        TextButton(
            onClick = onFirstButtonClick,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(firstButtonTitle)
        }
        TextButton(
            onClick = onSecondButtonClick,
            shape = MaterialTheme.shapes.medium
        ) {
            Text(secondButtonTitle)
        }
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
