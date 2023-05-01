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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.view.WindowCompat
import com.cmgapps.LogTag
import com.cmgapps.android.curriculumvitae.email.EMAIL_ADDRESS
import com.cmgapps.android.curriculumvitae.infra.di.Dispatcher
import com.cmgapps.android.curriculumvitae.infra.jni.CvNative
import com.cmgapps.android.curriculumvitae.ui.BadSignature
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.infra.di.CvDispatchers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import androidx.core.splashscreen.R as SplashscreenR

@LogTag
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @Dispatcher(CvDispatchers.Default)
    lateinit var defaultDispatchers: CoroutineDispatcher

    override fun onCreate(savedInstanceState: Bundle?) {
        with(TypedValue()) {
            theme.resolveAttribute(SplashscreenR.attr.postSplashScreenTheme, this, true)
            setTheme(resourceId)
        }
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        setContent {
            Theme {
                val snackbarHostState = remember { SnackbarHostState() }
                val coroutineScope = rememberCoroutineScope()

                val validSignature by produceState(initialValue = true, this) {
                    withContext(defaultDispatchers) {
                        value = CvNative.checkSignature(this@MainActivity)
                    }
                }

                if (validSignature) {
                    MainScreen(
                        snackbarHostState = snackbarHostState,
                        onFabClick = {
                            val intent = createEmailIntent()
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(getString(R.string.no_email))
                                }
                            }
                        },
                        onOpenWebsite = { uri ->
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                uri,
                            )
                            if (intent.resolveActivity(packageManager) != null) {
                                startActivity(intent)
                            }
                        },
                    )
                } else {
                    BadSignature()
                }
            }
        }
    }
}

private fun Context.createEmailIntent() = Intent(Intent.ACTION_SENDTO).apply {
    data = Uri.parse("mailto:")
    putExtra(Intent.EXTRA_EMAIL, arrayOf(EMAIL_ADDRESS))
    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.you_are_hired))
}
