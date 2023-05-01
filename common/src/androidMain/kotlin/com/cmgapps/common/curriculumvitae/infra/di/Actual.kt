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

package com.cmgapps.common.curriculumvitae.infra.di

import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.squareup.sqldelight.android.AndroidSqliteDriver
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun provideBaseUrl() = Url(BaseUrl)
actual fun platformModule() = module {
    single {
        DatabaseWrapper {
            AndroidSqliteDriver(it, androidContext(), "CvDb.db")
        }
    }
}

actual val IO = Dispatchers.IO
