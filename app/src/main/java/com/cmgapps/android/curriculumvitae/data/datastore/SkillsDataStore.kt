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

package com.cmgapps.android.curriculumvitae.data.datastore

import androidx.datastore.core.Serializer
import com.cmgapps.LogTag
import timber.log.Timber
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import com.cmgapps.common.curriculumvitae.data.domain.Skill as DomainSkill
import com.cmgapps.common.curriculumvitae.data.network.Skill as NetworkSkill

@Suppress("BlockingMethodInNonBlockingContext")
@LogTag("SkillsDataStore")
object SkillsDataStoreSerializer : Serializer<Skills?> {
    override val defaultValue: Skills? = null

    override suspend fun readFrom(input: InputStream): Skills? =
        try {
            Skills.ADAPTER.decode(input)
        } catch (exc: IOException) {
            Timber.tag(LOG_TAG).e(exc)
            null
        }

    override suspend fun writeTo(t: Skills?, output: OutputStream) {
        t?.let { Skills.ADAPTER.encode(output, it) }
    }
}

fun Skills.asDomainModel(): List<DomainSkill> = skills.map {
    DomainSkill(
        it.name,
        it.level,
    )
}

fun List<NetworkSkill>.asDataStoreModel(): Skills = Skills(
    map {
        Skill(
            it.name,
            it.level,
        )
    },
)
