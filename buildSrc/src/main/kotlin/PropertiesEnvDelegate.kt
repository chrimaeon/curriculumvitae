/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import org.gradle.api.Project
import java.io.File
import java.util.Locale
import java.util.Properties
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

class PropertyNotExistingException(message: String) : RuntimeException(message)

class PropertiesEnvDelegate(private val propertiesFile: File) : ReadOnlyProperty<Any?, String> {

    private val properties: Properties? = if (propertiesFile.exists()) {
        Properties().apply {
            propertiesFile.inputStream().use { load(it) }
        }
    } else null

    private fun String.toSnakeCase(): String =
        replace(camelCaseRegEx, "$1_$2").toUpperCase(Locale.ROOT)

    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        val envPropName = "CV_${property.name.toSnakeCase()}"

        return properties?.getProperty(property.name)
            ?: System.getenv(envPropName)
            ?: throw PropertyNotExistingException(
                "Property '${property.name}' not found in '${propertiesFile.canonicalPath}'" +
                    " or no Environment variable '$envPropName' found"
            )
    }

    companion object {
        private val camelCaseRegEx = "([a-z])([A-Z]+)".toRegex()
    }
}

inline val Project.configProperty: PropertiesEnvDelegate
    get() = PropertiesEnvDelegate(rootDir.resolve("config.properties"))

inline val Project.versionProperty: PropertiesEnvDelegate
    get() = PropertiesEnvDelegate(rootDir.resolve("version.properties"))
