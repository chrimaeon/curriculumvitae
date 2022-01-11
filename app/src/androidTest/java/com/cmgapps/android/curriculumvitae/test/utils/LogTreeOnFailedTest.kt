/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
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

package com.cmgapps.android.curriculumvitae.test.utils

import android.util.Log
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

internal abstract class LogTreeOnFailedTest {

    @get:Rule
    abstract val composeTestRule: ComposeTestRule

    @get:Rule
    val watchman: TestWatcher = object : TestWatcher() {
        override fun failed(e: Throwable, description: Description) {
            Log.e(
                this@LogTreeOnFailedTest::class.java.simpleName,
                "${description.displayName}\n${e.message}\n with tree\n${composeTestRule.onRoot().printToString()}",
            )
        }
    }
}
