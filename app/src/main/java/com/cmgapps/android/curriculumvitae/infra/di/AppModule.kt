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

import android.content.Context
import android.hardware.SensorManager
import androidx.core.content.getSystemService
import coil.ImageLoader
import coil.map.Mapper
import coil.util.CoilUtils
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.infra.AssetPath
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    @Language
    fun provideLanguage(): String = Locale.getDefault().language

    @Provides
    @Singleton
    fun provideImageLoader(
        @ApplicationContext context: Context,
        okHttpClient: OkHttpClient,
        @BaseUrl baseUrl: String,
    ): ImageLoader {
        val cachedClient =
            okHttpClient.newBuilder()
                .cache(CoilUtils.createDefaultCache(context))
                .apply {
                    if (BuildConfig.DEBUG) {
                        // Simulate loading
                        addInterceptor { chain ->
                            Thread.sleep(2000)
                            chain.proceed(chain.request())
                        }
                    }
                }
                .build()
        return ImageLoader.Builder(context)
            .okHttpClient(cachedClient)
            .componentRegistry {
                add(object : Mapper<AssetPath, HttpUrl> {
                    override fun map(data: AssetPath): HttpUrl {
                        return baseUrl.toHttpUrl().newBuilder(data.path)?.build()
                            ?: error("Cannot create asset url; baseUrl=$baseUrl, assetPath=${data.path}")
                    }
                })
            }
            .build()
    }

    @Provides
    @Singleton
    fun provideSensorManager(@ApplicationContext context: Context): SensorManager =
        context.getSystemService()!!
}
