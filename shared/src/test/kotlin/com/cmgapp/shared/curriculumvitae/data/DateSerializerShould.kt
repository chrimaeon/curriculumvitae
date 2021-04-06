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

package com.cmgapp.shared.curriculumvitae.data

import kotlinx.serialization.json.Json
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import java.util.GregorianCalendar

class DateSerializerShould {

    @Test
    fun `encode date`() {
        assertThat(
            Json.encodeToString(DateSerializer, GregorianCalendar(2021, 8, 2).time),
            `is`("\"2021-09-02\"")
        )
    }

    @Test
    fun `decode date`() {
        assertThat(
            Json.decodeFromString(DateSerializer, "\"2021-09-02\""),
            `is`(GregorianCalendar(2021, 8, 2).time)
        )
    }
}
