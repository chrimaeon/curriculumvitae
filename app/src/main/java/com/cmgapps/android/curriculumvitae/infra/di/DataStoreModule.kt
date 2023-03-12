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

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.cmgapps.android.curriculumvitae.data.datastore.OssProjects
import com.cmgapps.android.curriculumvitae.data.datastore.OssProjectsDataStoreSerializer
import com.cmgapps.android.curriculumvitae.data.datastore.Profile
import com.cmgapps.android.curriculumvitae.data.datastore.ProfileDataStoreSerializer
import com.cmgapps.android.curriculumvitae.data.datastore.Skills
import com.cmgapps.android.curriculumvitae.data.datastore.SkillsDataStoreSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {
    @Singleton
    @Provides
    fun provideProfileDataStore(@ApplicationContext context: Context): DataStore<Profile?> =
        DataStoreFactory.create(
            ProfileDataStoreSerializer,
        ) {
            context.dataStoreFile("profile.pb")
        }

    @Singleton
    @Provides
    fun provideSkillsDataStore(@ApplicationContext context: Context): DataStore<Skills?> =
        DataStoreFactory.create(
            SkillsDataStoreSerializer,
        ) {
            context.dataStoreFile("skills.pb")
        }

    @Singleton
    @Provides
    fun provideOssProjectsDataStore(@ApplicationContext context: Context): DataStore<OssProjects?> =
        DataStoreFactory.create(
            OssProjectsDataStoreSerializer,
        ) {
            context.dataStoreFile("oss_projects.pb")
        }
}
