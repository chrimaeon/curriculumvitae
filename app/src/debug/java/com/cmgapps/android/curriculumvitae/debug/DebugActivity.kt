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
import androidx.compose.ui.graphics.Color
import com.cmgapps.android.curriculumvitae.infra.di.DebugPreferences
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.android.curriculumvitae.ui.lightBlue700
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DebugActivity : ComponentActivity() {

    @Inject
    @DebugPreferences
    lateinit var debugPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme(
                darkSystemBarColor = Color(0xFF121212),
                lightSystemBarColor = lightBlue700,
            ) {
                DebugScreen(debugPreferences)
            }
        }
    }

    companion object {
        const val BASE_URL_KEY = "baseUrl"
    }
}
