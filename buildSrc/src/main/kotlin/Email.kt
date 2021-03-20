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

import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import java.io.File

fun generateEmailAddress(emailAddress: String, packageName: String, outputDir: File) {
    val xorFun = FunSpec.builder("xor").addModifiers(KModifier.PRIVATE)
        .addParameter("input", CharArray::class)
        .addParameter("key", CharArray::class)
        .returns(CharArray::class)
        .addStatement(
            """
              return input.mapIndexed { index, char ->
                (char.toInt() xor key[index %% key.size].toInt()).toChar()
              }.toCharArray()
            """.trimIndent()
        ).build()
    val emailCharsProperty = PropertySpec.builder("emailXored", CharArray::class)
        .addModifiers(KModifier.PRIVATE)
        .initializer(
            xor(
                emailAddress.toCharArray(),
                packageName.toCharArray()
            ).joinToString(prefix = "charArrayOf(", postfix = ")") {
                "${it.toInt()}.toChar()"
            }).build()

    val emailAddressProperty = PropertySpec.builder("EMAIL_ADDRESS", String::class)
        .initializer(
            "String(%N(%N, %S.toCharArray()))",
            xorFun,
            emailCharsProperty,
            packageName
        )
        .build()

    FileSpec.builder("${packageName}.email", "Email").apply {
        addFunction(xorFun)
        addProperty(emailCharsProperty)
        addProperty(emailAddressProperty)
    }.build()
        .writeTo(outputDir)
}

private fun xor(input: CharArray, key: CharArray) = input.mapIndexed { index, char ->
    (char.toInt() xor key[index % key.size].toInt()).toChar()
}.toCharArray()

