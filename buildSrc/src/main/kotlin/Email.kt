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

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.MemberName.Companion.member
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.asClassName
import java.io.File

fun generateEmailAddress(emailAddress: String, packageName: String, outputDir: File) {
    val charClassName = Char::class.asClassName()
    val mapIndexedMember = charClassName.member("mapIndexed")
    val charToIntMember = charClassName.member("toInt")
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
            |    (%3L.%4N() xor %5N[%2L %% %5N.%6N].%4N()).%7N()
            |}.%8N()
            """.trimMargin(),
            mapIndexedMember,
            indexVar,
            charVar,
            charToIntMember,
            keyParameter,
            sizeMember,
            intToCharMember,
            toCharArrayMember
        )
        .build()

    val charArrayOfMember = MemberName("kotlin", "charArrayOf")
    val emailCharsProperty = PropertySpec.builder("e", CharArray::class)
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            CodeBlock.builder().apply {
                add("%N(", charArrayOfMember)
                emailAddress.toCharArray().xor(packageName.toCharArray()).let {
                    val size = it.size
                    it.forEachIndexed { index, char ->
                        add("${char.toInt()}.%N()", intToCharMember)
                        if (index < size - 1) add(",")
                    }
                    add(")")
                }
            }.build()
        ).build()

    val stringToCharArrayMember = String::class.asClassName().member("toCharArray")
    val emailAddressProperty = PropertySpec.builder("EMAIL_ADDRESS", String::class)
        .initializer(
            "%T(%N.%N(%S.%N()))",
            String::class,
            emailCharsProperty,
            xorFun,
            packageName,
            stringToCharArrayMember
        )
        .build()

    FileSpec.builder("${packageName}.email", "Email").apply {
        addFunction(xorFun)
        addProperty(emailCharsProperty)
        addProperty(emailAddressProperty)
    }.build()
        .writeTo(outputDir)
}

private fun CharArray.xor(key: CharArray) = mapIndexed { index, char ->
    (char.toInt() xor key[index % key.size].toInt()).toChar()
}.toCharArray()

