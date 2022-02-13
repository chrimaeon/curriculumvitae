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

package com.cmgapps.wear.curriculumvitae

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipeLeft
import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.MainScope
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module
import org.koin.test.KoinTest

class MainActivityShould : KoinTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private lateinit var mockedModule: Module

    @Before
    fun beforeEach() {
        mockedModule = module {
            single { MockHttpClient() }
            factory {
                EmploymentRepository(
                    api = get(),
                    databaseWrapper = DatabaseWrapper {
                        AndroidSqliteDriver(CvDatabase.Schema, get(), /* in-memory */null)
                    },
                    logger = get { parametersOf("MockEmploymentRepository") },
                    scope = MainScope()
                )
            }
        }

        getKoin().loadModules(listOf(mockedModule), allowOverride = true)
    }

    @After
    fun afterEach() {
        getKoin().unloadModules(listOf(mockedModule))
    }

    @Test
    fun showProfile() {
        with(composeTestRule) {
            onNodeWithText("First Last").assertIsDisplayed()
        }
    }

    @Test
    fun showEmployments() {
        with(composeTestRule) {
            onRoot().performTouchInput { swipeLeft() }
            onNodeWithText("My Company").assertIsDisplayed()
        }
    }

    @Test
    fun showSkills() = with(composeTestRule) {
        onRoot().performTouchInput {
            swipeLeft()
            swipeLeft()
        }
        onNodeWithText("Skill level 1").assertIsDisplayed()
        return@with
    }
}
