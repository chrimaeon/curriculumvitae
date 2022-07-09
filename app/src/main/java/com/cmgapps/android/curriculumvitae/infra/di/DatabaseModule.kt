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

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.DescriptionAdapter
import com.cmgapps.common.curriculumvitae.data.db.Employment
import com.cmgapps.common.curriculumvitae.data.db.EmploymentQueries
import com.squareup.sqldelight.db.SqlDriver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Provides
    @Singleton
    fun provideEmploymentDao(database: CvDatabase): EmploymentQueries = database.employmentQueries

    @Provides
    @Singleton
    fun provideDatabase(driver: SqlDriver): CvDatabase = CvDatabase(
        driver,
        employmentAdapter = Employment.Adapter(
            descriptionAdapter = DescriptionAdapter,
        ),
    )
}
