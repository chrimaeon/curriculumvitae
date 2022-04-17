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

import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.Url
import io.ktor.serialization.kotlinx.json.json
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(
        httpClient: HttpClient,
        @BaseUrl baseUrl: String,
    ): CvApiService {
        return CvApiService(httpClient, baseUrl = Url(baseUrl))
    }

    @Provides
    @Singleton
    fun provideHttpClient(
        okHttpClient: OkHttpClient,
        @Language lang: String,
    ): HttpClient = HttpClient(OkHttp) {
        engine {
            preconfigured = okHttpClient.newBuilder()
                .addNetworkInterceptor { chain ->
                    chain.request().newBuilder().header("Accept-Language", lang).let {
                        chain.proceed(it.build())
                    }
                }
                // .addInterceptor {
                //     Thread.sleep(10000)
                //     it.proceed(it.request())
                // }
                .build()
        }
        install(ContentNegotiation) {
            json()
        }
    }
}
