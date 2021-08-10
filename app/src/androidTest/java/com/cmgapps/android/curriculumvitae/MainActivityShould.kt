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

import android.content.pm.ActivityInfo
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.cmgapps.android.curriculumvitae.test.StubNetworkEmployment
import com.cmgapps.android.curriculumvitae.test.StubNetworkProfile
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@LargeTest
class MainActivityShould {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun before() {
        composeTestRule.activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @Test
    fun showMainNavigation() = with(composeTestRule) {
        onNodeWithText(activity.resources.getString(R.string.profile_label)).assertIsDisplayed()
        onNodeWithText(activity.resources.getString(R.string.employment_label)).assertIsDisplayed()
        onNodeWithText(activity.resources.getString(R.string.skills_label)).assertIsDisplayed()
        return@with
    }

    @Test
    fun showFab() {
        composeTestRule.onNodeWithTag("Fab").assertIsDisplayed()
    }

    @Test
    fun showProfileScreen() = with(composeTestRule) {
        val profile = StubNetworkProfile()
        onNodeWithText(profile.name).assertIsDisplayed()
        onNodeWithText(profile.address.street).assertIsDisplayed()
        onNodeWithText(profile.address.city, substring = true).assertIsDisplayed()
        onNodeWithText(profile.address.postalCode, substring = true)
            .assertIsDisplayed()
        onNodeWithText(profile.email).assertIsDisplayed()
        onNodeWithText(profile.phone).assertIsDisplayed()
        assertThat(profile.intro.size, `is`(2))
        onNodeWithText(profile.intro[0], substring = true).assertIsDisplayed()
        onNodeWithText(profile.intro[1], substring = true).assertIsDisplayed()
        return@with
    }

    @Test
    fun moveToWork() = with(composeTestRule) {
        val employment = StubNetworkEmployment()
        onNodeWithText(activity.resources.getString(R.string.employment_label)).assertIsDisplayed()
            .performClick()
        onNodeWithTag("employmentCard${employment.hashCode()}").assertIsDisplayed()
        return@with
    }

    @Test
    fun moveToSkills() = with(composeTestRule) {

        onNodeWithText(activity.resources.getString(R.string.skills_label)).assertIsDisplayed()
            .performClick()
        onNodeWithText("Android").assertIsDisplayed()
        return@with
    }

    @Test
    fun openInfoBottomSheet() = with(composeTestRule) {
        onNodeWithText("Info").assertIsDisplayed().performClick()
        onNodeWithText("Curriculum Vitae").assertIsDisplayed()
        onNodeWithText("Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})").assertIsDisplayed()
        onNodeWithText("Copyright \u00A9 ${BuildConfig.BUILD_YEAR} Christian Grach").assertIsDisplayed()
        onNodeWithText("m.cmgapps.com").assertIsDisplayed().assertHasClickAction()
        onNodeWithText(activity.resources.getString(R.string.info_oss_licenses)).assertIsDisplayed()
            .assertHasClickAction()
        onNodeWithText(activity.resources.getString(R.string.info_open_font_licenses)).assertIsDisplayed()
            .assertHasClickAction()
        return@with
    }
}
