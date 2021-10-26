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

import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.squareup.sqldelight.db.SqlDriver
import com.squareup.sqldelight.sqlite.driver.JdbcSqliteDriver
import io.ktor.http.Url
import org.koin.dsl.module
import java.io.File
import java.util.prefs.Preferences

const val DebugBaseUrlKey = "debugBaseUrl"

actual fun provideBaseUrl(): Url {
    val prefs = Preferences.userRoot()
    return Url(prefs.get(DebugBaseUrlKey, BaseUrl))
}

actual fun platformModule() = module {
    single { DatabaseWrapper(::provideDbDriver) }
}

private fun provideDbDriver(schema: SqlDriver.Schema): SqlDriver {
    val databasePath = File(System.getProperty("java.io.tmpdir"), "CvDb.db")
    Logger.withTag("JdbcSqliteDriver").i { "Database path: $databasePath" }
    return JdbcSqliteDriver(url = "jdbc:sqlite:${databasePath.absolutePath}").also { driver ->
        schema.create(driver)
    }
}
