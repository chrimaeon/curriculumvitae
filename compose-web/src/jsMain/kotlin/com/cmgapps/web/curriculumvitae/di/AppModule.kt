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

package com.cmgapps.web.curriculumvitae.di

import com.cmgapps.common.curriculumvitae.data.network.CvApi
import com.cmgapps.web.curriculumvitae.baseUrl
import com.cmgapps.web.curriculumvitae.repository.EmploymentRepository
import com.cmgapps.web.curriculumvitae.repository.ProfileRepository
import com.cmgapps.web.curriculumvitae.repository.StatusRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.features.websocket.WebSockets
import io.ktor.http.Url
import kotlinx.browser.window
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.w3c.dom.get

private fun module() = org.koin.dsl.module {
    single { createHttpClient() }
    single { CvApi(get(), getBaseUrl()) }
    single { ProfileRepository(get()) }
    single { EmploymentRepository(get()) }
    single { StatusRepository(get()) }
}

private fun getBaseUrl(): Url = if (js("PRODUCTION").unsafeCast<Boolean>()) {
    Url(baseUrl)
} else {
    Url(window.localStorage["baseUrl"] ?: baseUrl)
}

private fun createHttpClient(): HttpClient = HttpClient(Js) {
    install(JsonFeature) {
        serializer = KotlinxSerializer()
    }
    install(WebSockets)
}

fun initKoin(): KoinApplication = startKoin {
    modules(module())
}
