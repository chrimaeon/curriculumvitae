/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
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
import com.cmgapps.common.curriculumvitae.data.domain.OssProject as DomainOssProject
import com.cmgapps.common.curriculumvitae.data.network.OssProject as NetworkOssProject

@Suppress("BlockingMethodInNonBlockingContext")
@LogTag("OssProjectsSerializer")
object OssProjectsDataStoreSerializer : Serializer<OssProjects?> {
    override val defaultValue: OssProjects? = null

    override suspend fun readFrom(input: InputStream): OssProjects? =
        try {
            OssProjects.ADAPTER.decode(input)
        } catch (exc: IOException) {
            Timber.tag(LOG_TAG).e(exc)
            null
        }

    override suspend fun writeTo(t: OssProjects?, output: OutputStream) {
        t?.let { OssProjects.ADAPTER.encode(output, it) }
    }
}

fun OssProjects.asDomainModel(): List<DomainOssProject> = ossProjects.map {
    DomainOssProject(
        name = it.name,
        description = it.description,
        topics = it.topics,
        url = it.url,
        stars = it.stars,
        private = it.private_,
        fork = it.fork,
        archived = it.archived,
    )
}

fun List<NetworkOssProject>.asDataStoreModel(): OssProjects = OssProjects(
    map {
        OssProject(
            name = it.name,
            description = it.description,
            topics = it.topics,
            url = it.url,
            stars = it.stars,
            private_ = it.private,
            fork = it.fork,
            archived = it.archived,
        )
    },
)
