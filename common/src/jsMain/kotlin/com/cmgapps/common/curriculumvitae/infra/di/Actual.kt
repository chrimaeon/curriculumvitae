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

import PRODUCTION
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.drivers.sqljs.initSqlDriver
import io.ktor.http.Url
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.koin.dsl.module
import org.w3c.dom.get

actual fun provideBaseUrl(): Url = if (PRODUCTION) {
    Url(BaseUrl)
} else {
    Url(window.localStorage["baseUrl"] ?: BaseUrl)
}

actual fun platformModule() = module {
    single { DatabaseWrapper(::provideDbDriver) }
}

private suspend fun provideDbDriver(schema: SqlDriver.Schema): SqlDriver =
    initSqlDriver(schema).await()
