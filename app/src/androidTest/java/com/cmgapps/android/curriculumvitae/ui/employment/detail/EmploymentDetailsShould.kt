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

package com.cmgapps.android.curriculumvitae.ui.employment.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.SavedStateHandle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cmgapps.android.curriculumvitae.infra.NavArguments
import com.cmgapps.android.curriculumvitae.test.StubDatabaseEmployment
import com.cmgapps.android.curriculumvitae.test.StubDomainEmployment
import com.cmgapps.android.curriculumvitae.test.utils.LogTreeOnFailedTest
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.common.curriculumvitae.data.db.EmploymentQueries
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.dropbox.android.external.store4.Store
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@LargeTest
internal class EmploymentDetailsShould : LogTreeOnFailedTest() {

    override val composeTestRule = createComposeRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    private lateinit var viewModel: EmploymentDetailViewModel

    @Inject
    lateinit var store: Store<Int, Employment>

    @Inject
    lateinit var employmentQueries: EmploymentQueries

    @Before
    fun before() {
        hiltRule.inject()
        val employment = StubDatabaseEmployment()
        employmentQueries.insertEmployment(employment)
        viewModel =
            EmploymentDetailViewModel(
                SavedStateHandle(mapOf(NavArguments.EMPLOYMENT_ID.argumentName to employment.id)),
                store
            )

        composeTestRule.setContent {
            Theme {
                EmploymentDetails(viewModel = viewModel, navigateUp = {})
            }
        }
    }

    @Test
    fun renderEmployment() {
        val employment = StubDomainEmployment()
        composeTestRule.onNodeWithText("Foo").assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.employer).assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.workPeriod.asHumanReadableString())
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.description.joinToString(separator = "\n\n"))
            .assertIsDisplayed()
    }
}
