/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.infra.victools

import com.github.victools.jsonschema.generator.MemberScope
import com.github.victools.jsonschema.generator.Module
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlin.reflect.full.memberProperties

class CustomModule : Module {

    override fun applyToConfigBuilder(builder: SchemaGeneratorConfigBuilder) {
        builder.forFields().apply {
            withRequiredCheck { true }
            withNullableCheck(::isNullableType)
            withTargetTypeOverridesResolver { field ->
                val context = field.context
                when (field.type.erasedType) {
                    LocalDate::class.java, Instant::class.java -> listOf(context.resolve(String::class.java))
                    else -> null
                }
            }
            withStringFormatResolver { field ->
                when (field.declaredType.erasedType) {
                    LocalDate::class.java -> "date"
                    Instant::class.java -> "date-time"
                    else -> null
                }
            }
        }
    }

    private fun isNullableType(fieldOrMethod: MemberScope<*, *>): Boolean {
        fieldOrMethod.declaringType.erasedType.kotlin.memberProperties.forEach { property ->
            if (property.name == fieldOrMethod.name) {
                return property.returnType.isMarkedNullable
            }
        }

        return false
    }
}
