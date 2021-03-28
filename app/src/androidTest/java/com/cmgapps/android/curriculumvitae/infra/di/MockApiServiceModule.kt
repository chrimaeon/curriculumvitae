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

import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.infra.CvApiService
import com.cmgapps.android.curriculumvitae.test.StubProfile
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import retrofit2.Retrofit
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import retrofit2.mock.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ApiServiceModule::class]
)
object MockApiServiceModule {

    @Provides
    @Singleton
    fun provideApiService(): CvApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .build()

        val networkBehavior = NetworkBehavior.create().apply {
            setDelay(0, TimeUnit.MILLISECONDS)
            setErrorPercent(0)
            setFailurePercent(0)
            setVariancePercent(0)
        }

        val delegate: BehaviorDelegate<CvApiService> =
            MockRetrofit.Builder(retrofit).networkBehavior(networkBehavior).build().create()
        return MockCvApiService(delegate)
    }
}

private class MockCvApiService(private val delegate: BehaviorDelegate<CvApiService>) :
    CvApiService {
    override suspend fun getProfile() = delegate.returningResponse(StubProfile()).getProfile()
}
