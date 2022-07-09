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

package com.cmgapps.android.curriculumvitae.infra.di

import androidx.datastore.core.DataStore
import com.cmgapps.android.curriculumvitae.data.datastore.Profile
import com.cmgapps.android.curriculumvitae.data.datastore.Skills
import com.cmgapps.android.curriculumvitae.test.StubDataStoreProfile
import com.cmgapps.android.curriculumvitae.test.StubDataStoreSkills
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataStoreModule::class],
)
class FakeDataStoreModule {

    @Provides
    fun provideProfileDataStore(): DataStore<Profile?> =
        object : DataStore<Profile?> {

            private var profile: Profile? = StubDataStoreProfile()

            private val profileFlow = MutableStateFlow(profile)

            override val data: Flow<Profile?> = profileFlow

            override suspend fun updateData(transform: suspend (t: Profile?) -> Profile?) =
                transform(profile).also {
                    profile = it
                    profileFlow.value = it
                }
        }

    @Provides
    fun provideSkillsDataStore(): DataStore<Skills?> =
        object : DataStore<Skills?> {

            private var skills: Skills? = StubDataStoreSkills()

            private val skillsFlow = MutableStateFlow(skills)

            override val data: Flow<Skills?> = skillsFlow

            override suspend fun updateData(transform: suspend (t: Skills?) -> Skills?) =
                transform(skills).also {
                    skills = it
                    skillsFlow.value = it
                }
        }
}
