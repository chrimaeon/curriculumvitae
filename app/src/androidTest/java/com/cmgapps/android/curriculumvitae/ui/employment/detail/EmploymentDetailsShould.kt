/*
 * Copyright (c) 2021. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
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
internal class EmploymentDetailsShould {

    @get:Rule(order = 1)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 2)
    val composeTestRule = createComposeRule()

    @get:Rule(order = 3)
    val logger = LogTreeOnFailedTest(composeTestRule)

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
                store,
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
        composeTestRule.onNodeWithText(employment.jobTitle).assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.employer).assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.workPeriod.asHumanReadableString())
            .assertIsDisplayed()
        composeTestRule.onNodeWithText(employment.description.joinToString(separator = "\n\n"))
            .assertIsDisplayed()
    }
}
