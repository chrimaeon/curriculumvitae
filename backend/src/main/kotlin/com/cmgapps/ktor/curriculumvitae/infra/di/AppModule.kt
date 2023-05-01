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

package com.cmgapps.ktor.curriculumvitae.infra.di

import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.DescriptionAdapter
import com.cmgapps.common.curriculumvitae.data.db.Employment
import com.cmgapps.ktor.curriculumvitae.ClassLoaderModelLoader
import com.cmgapps.ktor.curriculumvitae.ModelLoader
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf<ModelLoader>(::ClassLoaderModelLoader)
    single<SqlDriver> {
        JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY).also { driver ->
            CvDatabase.Schema.create(driver)
        }
    }
    single {
        CvDatabase(
            get(),
            employmentAdapter = Employment.Adapter(
                descriptionAdapter = DescriptionAdapter,
            ),
        )
    }
    single { Dispatchers.IO }
}
