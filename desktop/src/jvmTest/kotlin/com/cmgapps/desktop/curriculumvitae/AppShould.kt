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

package com.cmgapps.desktop.curriculumvitae

import StubDomainEmployment
import StubDomainOssProject
import StubDomainProfile
import StubDomainSkill
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 *
 *   TODO: replace [assertExists][androidx.compose.ui.test.SemanticsNodeInteraction.assertExists] with
 *   [assertIsDisplayed][androidx.compose.ui.test.assertIsDisplayed] once available
 *
 */
@OptIn(ExperimentalCoroutinesApi::class)
internal class AppShould {

    @get:Rule
    val composeRule = createComposeRule()

    private val profile = StubDomainProfile()
    private val skill = StubDomainSkill()
    private val employment = StubDomainEmployment()
    private val project = StubDomainOssProject()

    @Before
    fun beforeEach() {
        composeRule.setContent {
            App(
                profile = profile,
                profileImage = ImageBitmap(500, 500),
                employments = listOf(employment),
                skills = listOf(skill),
                projects = listOf(project),
                backgroundImage = ImageBitmap(1200, 1200),
            )
        }
    }

    @Test
    fun `show Profile header`() = runTest {
        with(composeRule) {
            onNodeWithText(profile.name).assertExists()
            onNodeWithText(profile.email).assertExists()
            onNodeWithText(profile.phone).assertExists()
        }
    }

    @Test
    fun `show Employments`() = runTest {
        with(composeRule) {
            onNodeWithText(employment.employer).assertExists()
            onNodeWithText(
                employment.workPeriod.asHumanReadableString(),
            ).assertExists()
            onNodeWithText(employment.jobTitle).assertExists()
        }
    }

    @Test
    fun `show Skills`() = runTest {
        with(composeRule) {
            onNodeWithText(skill.name).assertExists()
        }
    }

    @Test
    fun `show OssProjects`() = runTest {
        with(composeRule) {
            onNodeWithText(project.name).assertExists()
            onNodeWithText(project.stars.toString()).assertExists()
            onNodeWithText(project.description).assertExists()
        }
    }
}
