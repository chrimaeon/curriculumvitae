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

import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.language
import com.cmgapps.common.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.common.curriculumvitae.repository.ProfileRepository
import com.cmgapps.common.curriculumvitae.repository.StatusRepository
import io.ktor.client.HttpClient
import io.ktor.client.features.defaultRequest
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.client.request.headers
import io.ktor.http.HttpHeaders
import io.ktor.http.Url
import kotlinx.coroutines.MainScope
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.dsl.KoinAppDeclaration

private val module = org.koin.dsl.module {
    single { createHttpClient() }
    single { CvApiService(get(), provideBaseUrl()) }
    single { ProfileRepository(get()) }
    single { EmploymentRepository(get(), get(), MainScope()) }
    single { StatusRepository(get()) }
}

private fun createHttpClient(): HttpClient = HttpClient {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(WebSockets)

    defaultRequest {
        headers {
            append(HttpHeaders.AcceptLanguage, language)
        }
    }
}

expect fun provideBaseUrl(): Url
expect fun platformModule(): Module

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication = startKoin {
    appDeclaration()
    modules(module, platformModule())
}
