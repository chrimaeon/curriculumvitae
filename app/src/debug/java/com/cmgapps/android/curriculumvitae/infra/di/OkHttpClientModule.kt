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

import android.annotation.SuppressLint
import android.content.Context
import androidx.preference.PreferenceManager
import com.cmgapps.android.curriculumvitae.debug.DebugActivity.Companion.BASE_URL_KEY
import com.cmgapps.common.curriculumvitae.BaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.security.cert.X509Certificate
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager

@Module
@InstallIn(SingletonComponent::class)
object OkHttpClientModule {

    @SuppressLint("LogDebugConditional")
    @SuppressWarnings("kotlin:S5527")
    @Provides
    @Singleton
    fun provideOkHttpClient(
        sslSocketFactory: SSLSocketFactory,
        trustManager: X509TrustManager,
    ): OkHttpClient =
        OkHttpClient.Builder()
            .sslSocketFactory(sslSocketFactory, trustManager)
            .hostnameVerifier { _, _ -> true }
            .addNetworkInterceptor(
                HttpLoggingInterceptor { message ->
                    Timber.tag("HttpLoggingInterceptor").d(message)
                }.apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            ).build()

    @Provides
    @SuppressLint("TrustAllX509TrustManager", "CustomX509TrustManager")
    @SuppressWarnings("kotlin:S4830")
    fun provideTrustManager(): X509TrustManager {
        return object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // do nothing
            }

            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                // do nothing
            }

            override fun getAcceptedIssuers() = emptyArray<X509Certificate>()
        }
    }

    @Provides
    @SuppressWarnings("kotlin:S4423")
    fun provideSslSocketFactory(trustManager: X509TrustManager): SSLSocketFactory {
        val sslContext = SSLContext.getInstance("SSL")
        sslContext.init(null, arrayOf(trustManager), java.security.SecureRandom())
        return sslContext.socketFactory
    }

    @Provides
    @BaseUrl
    @Singleton
    fun provideBaseUrl(@ApplicationContext context: Context): String {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getString(BASE_URL_KEY, BaseUrl)!!
    }
}
