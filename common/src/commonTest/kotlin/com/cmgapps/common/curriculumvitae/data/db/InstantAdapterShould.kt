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

package com.cmgapps.common.curriculumvitae.data.db

import kotlinx.datetime.Instant
import kotlin.test.Test
import kotlin.test.assertEquals

class InstantAdapterShould {
    private val time = "1979-09-02T09:00:00Z"

    @Test
    fun encode_instant() {
        assertEquals(time, InstantAdapter.encode(Instant.parse(time)))
    }

    @Test
    fun decode_instant() {
        assertEquals(time, InstantAdapter.decode(time).toString())
    }

    @Test
    fun round_trip_from_list() {
        val instant = Instant.parse(time)
        assertEquals(instant, InstantAdapter.decode(InstantAdapter.encode(instant)))
    }

    @Test
    fun round_trip_from_string() {
        assertEquals(time, InstantAdapter.encode(InstantAdapter.decode(time)))
    }
}
