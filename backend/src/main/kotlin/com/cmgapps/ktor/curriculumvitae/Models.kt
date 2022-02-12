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

package com.cmgapps.ktor.curriculumvitae

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

enum class Language {
    EN,
    DE
}

interface ModelLoader {
    fun <T> loadModel(
        serializer: KSerializer<T>,
        filePath: String,
    ): T?
}

class ClassLoaderModelLoader : ModelLoader {
    private val classLoader = this.javaClass.classLoader

    override fun <T> loadModel(serializer: KSerializer<T>, filePath: String): T? =
        classLoader
            .getResourceAsStream(filePath)
            ?.use { inputStream ->
                inputStream.bufferedReader()
                    .use { reader -> Json.decodeFromString(serializer, reader.readText()) }
            }
}
