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

import StubNetworkEmployment
import StubNetworkProfile
import StubNetworkSkill
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.cmgapps.common.curriculumvitae.data.domain.asDomainModel
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.data.network.asDatabaseModel
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.utils.io.ByteReadChannel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.KoinTestRule
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.whenever

/**
 *
 *   TODO: replace [assertExists][androidx.compose.ui.test.SemanticsNodeInteraction.assertExists] with
 *   [assertIsDisplayed][androidx.compose.ui.test.assertIsDisplayed] once available
 *
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
internal class AppShould : KoinTest {

    @get:Rule
    val composeRule = createComposeRule()

    @get:Rule
    val koinTestRule = KoinTestRule.create {
        modules(
            module {
                single {
                    DatabaseWrapper { schema ->
                        sqliteDriver.also {
                            schema.create(it)
                        }
                    }
                }
                factory { ProfileRepository(api) }
                factory {
                    EmploymentRepository(
                        api,
                        databaseWrapper = get(),
                        kermitLogger,
                        MainScope(),
                    )
                }
                factory { SkillsRepository(api) }
            },
        )
    }

    @Mock
    lateinit var api: CvApiService

    @Mock
    lateinit var kermitLogger: Logger

    private val profile = StubNetworkProfile()
    private val skill = StubNetworkSkill()
    private val employment = StubNetworkEmployment()

    private val sqliteDriver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)

    @Before
    fun beforeEach() {
        runBlocking {
            whenever(api.getProfile()) doReturn profile
            whenever(api.getAsset(any())) doReturn ByteReadChannel.Empty
            whenever(api.getSkills()) doReturn listOf(skill)
            whenever(api.getEmployments()) doReturn listOf(StubNetworkEmployment())
        }
        composeRule.setContent {
            App(koinTestRule.koin)
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
                employment.asDatabaseModel().asDomainModel().workPeriod.asHumanReadableString(),
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
}
