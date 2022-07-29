/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle.curriculumvitae

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import org.gradle.api.DefaultTask
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

@CacheableTask
abstract class ObfuscateEmailTask @Inject constructor(objects: ObjectFactory) : DefaultTask() {

    @get:Input
    val emailAddress: Property<String> = objects.property()

    @get:Input
    val packageName: Property<String> = objects.property()

    @get:OutputDirectory
    val outputDir: DirectoryProperty = objects.directoryProperty()

    @TaskAction
    fun obfuscate() {
        val charClassName = Char::class.asClassName()
        val mapIndexedMember = charClassName.member("mapIndexed")
        val charCodeMember = charClassName.member("code")
        val intToCharMember = Int::class.asClassName().member("toChar")
        val toCharArrayMember = MemberName("kotlin.collections", "toCharArray")
        val sizeMember = CharArray::class.asClassName().member("size")

        val keyParameter = ParameterSpec.builder("key", CharArray::class).build()
        val indexVar = "idx"
        val charVar = "c"

        val xorFun = FunSpec.builder("x").addModifiers(KModifier.PRIVATE)
            .receiver(CharArray::class)
            .returns(CharArray::class)
            .addParameter(keyParameter)
            .addStatement(
                """
                    |return %1N { %2L, %3L ->
                    |    (%3L.%4N xor %5N[%2L %% %5N.%6N].%4N).%7N()
                    |}.%8N()
                """.trimMargin(),
                mapIndexedMember,
                indexVar,
                charVar,
                charCodeMember,
                keyParameter,
                sizeMember,
                intToCharMember,
                toCharArrayMember,
            )
            .build()

        val packageName: String by packageName

        val charArrayOfMember = MemberName("kotlin", "charArrayOf")
        val emailCharsProperty = PropertySpec.builder("e", CharArray::class)
            .addModifiers(KModifier.PRIVATE)
            .initializer(
                CodeBlock.builder().apply {
                    add("%N(", charArrayOfMember)
                    emailAddress.get().toCharArray().xor(packageName.toCharArray()).let {
                        val size = it.size
                        it.forEachIndexed { index, char ->
                            add("${char.toInt()}.%N()", intToCharMember)
                            if (index < size - 1) add(",")
                        }
                        add(")")
                    }
                }.build(),
            ).build()

        val stringToCharArrayMember = String::class.asClassName().member("toCharArray")
        val emailAddressProperty = PropertySpec.builder("EMAIL_ADDRESS", String::class)
            .initializer(
                "%T(%N.%N(%S.%N()))",
                String::class,
                emailCharsProperty,
                xorFun,
                packageName,
                stringToCharArrayMember,
            )
            .build()

        FileSpec.builder("$packageName.email", "Email").apply {
            addFunction(xorFun)
            addProperty(emailCharsProperty)
            addProperty(emailAddressProperty)
        }.build().writeTo(outputDir.get().asFile)
    }
}

private fun CharArray.xor(key: CharArray) = mapIndexed { index, char ->
    (char.toInt() xor key[index % key.size].toInt()).toChar()
}.toCharArray()
