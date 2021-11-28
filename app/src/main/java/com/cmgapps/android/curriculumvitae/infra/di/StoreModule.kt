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

import androidx.datastore.core.DataStore
import com.cmgapps.android.curriculumvitae.data.datastore.asDataStoreModel
import com.cmgapps.android.curriculumvitae.data.datastore.asDomainModel
import com.cmgapps.common.curriculumvitae.data.db.EmploymentQueries
import com.cmgapps.common.curriculumvitae.data.domain.employmentMapper
import com.cmgapps.common.curriculumvitae.data.network.CvApiService
import com.cmgapps.common.curriculumvitae.data.network.asDatabaseModel
import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.Store
import com.dropbox.android.external.store4.StoreBuilder
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import com.squareup.sqldelight.runtime.coroutines.mapToOne
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.map
import javax.inject.Singleton
import com.cmgapps.android.curriculumvitae.data.datastore.Profile as DataStoreProfile
import com.cmgapps.common.curriculumvitae.data.db.Employment as DatabaseEmployment
import com.cmgapps.common.curriculumvitae.data.domain.Employment as DomainEmployment
import com.cmgapps.common.curriculumvitae.data.domain.Profile as DomainProfile

@Module
@InstallIn(SingletonComponent::class)
object StoreModule {

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideProfileStore(
        dataStore: DataStore<DataStoreProfile?>,
        api: CvApiService
    ): Store<String, DomainProfile> = StoreBuilder.from(
        fetcher = Fetcher.of<String, DataStoreProfile> { api.getProfile().asDataStoreModel() },
        sourceOfTruth = SourceOfTruth.of(
            reader = { dataStore.data.map { it?.asDomainModel() } },
            writer = { _, data -> dataStore.updateData { data } },
        )
    ).build()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideEmploymentListStore(
        employmentQueries: EmploymentQueries,
        api: CvApiService
    ): Store<String, List<DomainEmployment>> =
        StoreBuilder.from(
            fetcher = Fetcher.of<String, List<DatabaseEmployment>> {
                api.getEmployments().asDatabaseModel()
            },
            sourceOfTruth = SourceOfTruth.of(
                reader = {
                    employmentQueries.selectAllOrderedByStartDate(::employmentMapper).asFlow().mapToList()
                },
                writer = { _, employments ->
                    employmentQueries.transaction {
                        employments.forEach {
                            employmentQueries.insertEmployment(it)
                        }
                    }
                }
            )
        ).build()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    @Provides
    @Singleton
    fun provideEmploymentStore(
        employmentQueries: EmploymentQueries,
    ): Store<Int, DomainEmployment> =
        StoreBuilder.from(
            fetcher = Fetcher.ofFlow { id: Int ->
                employmentQueries.getEmployment(id, ::employmentMapper).asFlow().mapToOne()
            }
        ).build()
}
