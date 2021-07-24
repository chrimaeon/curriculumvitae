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

import com.cmgapps.android.curriculumvitae.network.CvApiService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServiceModule {

    @OptIn(ExperimentalSerializationApi::class)
    @Provides
    @Singleton
    fun provideApiService(
        okHttpClient: OkHttpClient,
        @Language lang: String,
        @BaseUrl baseUrl: String,
    ): CvApiService {
        val interceptedClient = okHttpClient.newBuilder()
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

        return Retrofit.Builder()
            .client(interceptedClient)
            .baseUrl(baseUrl)
            .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
            .build().run {
                create(CvApiService::class.java)
            }
    }
}
