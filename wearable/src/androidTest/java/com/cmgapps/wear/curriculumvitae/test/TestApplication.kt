/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.wear.curriculumvitae.test

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.cmgapps.common.curriculumvitae.data.db.CvDatabase
import com.cmgapps.common.curriculumvitae.data.db.DatabaseWrapper
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.wear.curriculumvitae.MockHttpClient
import com.cmgapps.wear.curriculumvitae.infra.di.appModule
import com.squareup.sqldelight.android.AndroidSqliteDriver
import kotlinx.coroutines.MainScope
import org.koin.android.ext.koin.androidContext
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

class TestApplication : Application() {
    private val testModule = module {
        single { MockHttpClient() }
        factory {
            EmploymentRepository(
                api = get(),
                databaseWrapper = DatabaseWrapper {
                    @Suppress("ktlint:standard:comment-wrapping")
                    AndroidSqliteDriver(CvDatabase.Schema, get(), /* in-memory */null)
                },
                logger = get { parametersOf("MockEmploymentRepository") },
                scope = MainScope(),
            )
        }
    }

    override fun onCreate() {
        super.onCreate()

        initKoin(enableNetworkLogging = true) {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(appModule, testModule)
        }
    }
}
