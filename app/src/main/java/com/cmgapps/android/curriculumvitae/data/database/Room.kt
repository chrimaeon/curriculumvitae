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

package com.cmgapps.android.curriculumvitae.data.database

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EmploymentDao {

    @Transaction
    @Query("SELECT * FROM employment")
    abstract fun getEmployments(): Flow<List<EmploymentWithDescription>>

    @Transaction
    open suspend fun insertAll(employments: List<EmploymentWithDescription>) {
        employments.forEach {
            insertEmployment(it.employment)
            insertDescription(it.description)
        }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertEmployment(employment: Employment)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertDescription(descriptions: List<Description>)
}

@Database(entities = [Employment::class, Description::class], version = 1)
@TypeConverters(Converters::class)
abstract class CvDatabase : RoomDatabase() {
    abstract val employmentDao: EmploymentDao
}
