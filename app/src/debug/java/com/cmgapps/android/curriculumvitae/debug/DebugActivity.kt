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

package com.cmgapps.android.curriculumvitae.debug

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.debug.DebugActivity.Companion.BASE_URL_KEY
import com.cmgapps.android.curriculumvitae.infra.di.BaseUrlPreferences
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.android.curriculumvitae.ui.Typography
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DebugActivity : ComponentActivity() {

    @Inject
    @BaseUrlPreferences
    lateinit var baseUrlPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = {
                                Text(
                                    text = "Debug",
                                    style = Typography.h6.copy(fontFamily = FontFamily.Default)
                                )
                            }
                        )
                    }
                ) {
                    Column(
                        modifier = Modifier
                            .padding(it)
                            .padding(16.dp)
                    ) {
                        BackendDropdown(baseUrlPreferences)
                    }
                }
            }
        }
    }

    companion object {
        const val BASE_URL_KEY = "baseUrl"
    }
}

@Composable
fun BackendDropdown(baseUrlPreferences: SharedPreferences) {
    val currentBaseUrl = baseUrlPreferences.getString(BASE_URL_KEY, BuildConfig.BASE_URL)
    val baseUrls = BuildConfig.DEBUG_BASE_URLS

    var expanded by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(baseUrls.indexOf(currentBaseUrl)) }

    Column {
        Text("Base URL", style = MaterialTheme.typography.overline)
        OutlinedButton(
            onClick = { expanded = true }
        ) {
            Text(text = baseUrls[selectedItemIndex])
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            BuildConfig.DEBUG_BASE_URLS.forEachIndexed { index, url ->
                DropdownMenuItem(
                    onClick = {
                        baseUrlPreferences.edit().putString(BASE_URL_KEY, url).apply()
                        selectedItemIndex = index
                        expanded = false
                    }
                ) {
                    Text(url)
                }
            }
        }
    }
}
