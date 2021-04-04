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

import org.gradle.api.Project
import java.io.File
import java.util.Locale
import java.util.Properties
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PropertiesEnvDelegate(propertiesFile: File) : ReadOnlyProperty<Any?, String?> {

    private val properties: Properties? = if (propertiesFile.exists()) {
        Properties().apply {
            propertiesFile.inputStream().use { load(it) }
        }
    } else null

    private fun String.toSnakeCase(): String =
        replace(camelCaseRegEx, "$1_$2").toUpperCase(Locale.ROOT)

    override fun getValue(thisRef: Any?, property: KProperty<*>): String? {
        return properties?.getProperty(property.name)
            ?: System.getenv("CV_${property.name.toSnakeCase()}")
    }

    companion object {
        private val camelCaseRegEx = "([a-z])([A-Z]+)".toRegex()
    }
}

fun Project.apiProperties() = PropertiesEnvDelegate(projectDir.resolve("api.properties"))
