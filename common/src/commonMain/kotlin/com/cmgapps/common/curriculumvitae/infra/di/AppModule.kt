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

import co.touchlab.kermit.StaticConfig
import co.touchlab.kermit.platformLogWriter
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.language
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.common.curriculumvitae.repository.SkillsRepository
import com.cmgapps.common.curriculumvitae.repository.StatusRepository
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logging
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.coroutines.MainScope
import org.koin.core.Koin
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import org.koin.dsl.KoinAppDeclaration
import co.touchlab.kermit.Logger as KermitLogger
import io.ktor.client.features.logging.Logger as KtorLogger

private fun module(enableNetworkLogging: Boolean) = org.koin.dsl.module {
    single {
        createHttpClient(
            enableNetworkLogging,
            get { parametersOf("HttpClient") },
        )
    }
    single {
        CvApiService(
            client = get(),
            baseUrl = provideBaseUrl()
        )
    }
    factory {
        ProfileRepository(
            api = get()
        )
    }
    factory {
        EmploymentRepository(
            api = get(),
            databaseWrapper = get(),
            logger = get { parametersOf("EmploymentRepository") },
            scope = MainScope(),
        )
    }
    factory {
        SkillsRepository(
            api = get()
        )
    }
    factory {
        StatusRepository(
            api = get()
        )
    }
    val baseLogger =
        co.touchlab.kermit.Logger(
            config = StaticConfig(logWriterList = listOf(platformLogWriter())),
            tag = "CurriculumVitae",
        )

    factory { (tag: String?) -> tag?.let { baseLogger.withTag(tag) } ?: baseLogger }
}

private fun createHttpClient(
    enableNetworkLogging: Boolean,
    kermitLogger: KermitLogger,
): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }

    install(WebSockets)

    if (enableNetworkLogging) {
        install(Logging) {
            level = LogLevel.ALL
            logger = object : KtorLogger {
                override fun log(message: String) {
                    kermitLogger.i(message)
                }
            }
        }
    }

    defaultRequest {
        header(
            HttpHeaders.AcceptLanguage,
            language
        )
    }
}

expect fun provideBaseUrl(): Url
expect fun platformModule(): Module

fun initKoin(
    enableNetworkLogging: Boolean = false,
    appDeclaration: KoinAppDeclaration = {}
): KoinApplication = startKoin {
    appDeclaration()
    modules(module(enableNetworkLogging), platformModule())
}

/**
 * used by native to easily create [KoinApplication]
 */
fun initKoinNative() = initKoin(enableNetworkLogging = true) { }

/**
 * Easy getter for [ProfileRepository] from [Koin]
 */
@Suppress("unused")
fun Koin.getProfileRepository() = get<ProfileRepository>()

/**
 * Easy getter for [EmploymentRepository] from [Koin]
 */
@Suppress("unused")
fun Koin.getEmploymentRepository() = get<EmploymentRepository>()
