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

@file:Suppress("ClassName")

package com.cmgapps.common.curriculumvitae.resource

import com.cmgapps.common.curriculumvitae.format
import com.cmgapps.common.curriculumvitae.language

fun plurals(resource: Plural, value: Int): String =
    if (value == 1) resource.one.format(value) else resource.other.format(value)

interface Plural {
    val one: String
    val other: String
}

object R {
    object plurals {
        object months : Plural {
            override val one: String
            override val other: String

            init {
                println(language)
                if (language == "de") {
                    one = "{0} Monat"
                    other = "{0} Monate"
                } else {
                    one = "{0} Month"
                    other = "{0} Months"
                }
            }
        }

        object years : Plural {
            override val one: String
            override val other: String

            init {
                if (language == "de") {
                    one = "{0} Jahr"
                    other = "{0} Jahre"
                } else {
                    one = "{0} Year"
                    other = "{0} Years"
                }
            }
        }
    }
}
