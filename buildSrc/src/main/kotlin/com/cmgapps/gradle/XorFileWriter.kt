/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okio.BufferedSink
import okio.buffer
import okio.sink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class XorFileWriter(private val source: File) {

    @Throws(IOException::class)
    fun write(outputFile: File) {
        try {
            FileOutputStream(outputFile).sink()
                .buffer()
                .use { sink ->

                    val date = ZonedDateTime.now(ZoneOffset.UTC)
                        .format(
                            DateTimeFormatter.ofLocalizedDateTime(
                                FormatStyle.SHORT,
                                FormatStyle.LONG,
                            )
                        )
                    sink.writeUtf8(
                        """/*
                        | * Generated with ${this::class.java.simpleName} $date
                        | * DO NOT EDIT
                        | */
                        |
                        |#ifndef $DEFINE_NAME
                        |#define $DEFINE_NAME
                        |
                        |
                        """.trimMargin()
                    )

                    Json.decodeFromString<List<Name>>(source.source().buffer().readUtf8())
                        .forEach { it.writeXorEncodedArray(sink) }

                    sink.writeUtf8("#endif //$DEFINE_NAME")
                }
        } catch (exc: IOException) {
            outputFile.delete()
            throw exc
        }
    }

    companion object {
        private const val DEFINE_NAME = "__JNI_DATA_FILE__"
    }
}

@Serializable
data class Name(var name: String, var id: String) {
    fun writeXorEncodedArray(sink: BufferedSink) {

        try {
            sink.writeUtf8("// $name\n")
                .writeUtf8("const unsigned char ")
                .writeUtf8(id)
                .writeUtf8("[] = ")
            val array = name.toByteArray().xor(KEY)
            sink.writeUtf8(
                array.toList().joinToString(prefix = "{", postfix = "}") {
                    "0x${it.toString(16)}"
                }
            )
                .writeUtf8(";\n")
                .writeUtf8("const size_t ")
                .writeUtf8(id)
                .writeUtf8("_LEN = ")
                .writeDecimalLong(array.size.toLong())
                .writeUtf8(";")
                .writeUtf8(System.lineSeparator())
        } catch (exc: IOException) {
            throw RuntimeException(exc)
        }
    }

    companion object {
        @JvmStatic
        private val KEY = "Curriculum Vitae".toByteArray()
    }
}

private fun ByteArray.xor(key: ByteArray): ByteArray = mapIndexed { index, char ->
    (char.toInt() xor key[index % key.size].toInt()).toByte()
}.toByteArray()

infix fun File.writeXorTo(output: File) = XorFileWriter(this).write(output)
