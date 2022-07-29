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

package com.cmgapps.wear.curriculumvitae.infra.di

import android.content.Context
import co.touchlab.kermit.Logger
import coil.ComponentRegistry
import coil.ImageLoader
import coil.disk.DiskCache
import coil.map.Mapper
import com.cmgapps.common.curriculumvitae.infra.di.provideBaseUrl
import com.cmgapps.wear.curriculumvitae.BuildConfig
import com.cmgapps.wear.curriculumvitae.infra.AssetPath
import com.cmgapps.wear.curriculumvitae.ui.employment.EmploymentViewModel
import com.cmgapps.wear.curriculumvitae.ui.profile.ProfileViewModel
import com.cmgapps.wear.curriculumvitae.ui.skills.SkillsViewModel
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val appModule = module {
    viewModel { ProfileViewModel(get()) }
    viewModel { EmploymentViewModel(get()) }
    viewModel { SkillsViewModel(get()) }
    single {
        val context: Context = get()
        ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .apply {
                        if (BuildConfig.DEBUG) {
                            val logger: Logger = get { parametersOf("ImageLoader") }
                            addNetworkInterceptor(
                                HttpLoggingInterceptor { message ->
                                    logger.d(message)
                                }.apply {
                                    level = HttpLoggingInterceptor.Level.BODY
                                },
                            )
                        }
                    }
                    .build()
            }
            .components(
                fun ComponentRegistry.Builder.() {
                    add(
                        Mapper<AssetPath, HttpUrl> { data, _ ->
                            provideBaseUrl().toString().toHttpUrl().newBuilder(data.path)
                                ?.build()
                                ?: error("asset url cannot be created; baseUrl=${provideBaseUrl()}, assetPath=${data.path}")
                        },
                    )
                },
            ).diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .build()
            }.build()
    }
}
