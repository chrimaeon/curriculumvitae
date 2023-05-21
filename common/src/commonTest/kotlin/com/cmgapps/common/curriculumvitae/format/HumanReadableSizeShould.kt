/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.format

import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals

class HumanReadableSizeShould {

    @Test
    fun get_zero() {
        assertEquals("0 B", 0L.humanReadableSize())
    }

    @Test
    fun get_negative() {
        assertEquals("-54 B", (-54L).humanReadableSize())
    }

    @Test
    fun get_bytes() {
        assertEquals("1000 B", 1000L.humanReadableSize())
    }

    @Test
    fun get_kilobytes() {
        assertEquals("1.00 KB", 2f.pow(10).toLong().humanReadableSize())
    }

    @Test
    fun get_arbitrary_kilobytes() {
        assertEquals("4.88 KB", 5000L.humanReadableSize())
    }

    @Test
    fun get_megabytes() {
        assertEquals("1.00 MB", 2f.pow(20).toLong().humanReadableSize())
    }

    @Test
    fun get_gigabytes() {
        assertEquals("1.00 GB", 2f.pow(30).toLong().humanReadableSize())
    }

    @Test
    fun get_terabytes() {
        assertEquals("1.00 TB", 2f.pow(40).toLong().humanReadableSize())
    }

    @Test
    fun get_petabytes() {
        assertEquals("1.00 PB", 2F.pow(50).toLong().humanReadableSize())
    }

    @Test
    fun get_exabytes() {
        assertEquals("1.00 EB", 2F.pow(60).toLong().humanReadableSize())
    }

    @Test
    fun get_max() {
        assertEquals("8.00 EB", Long.MAX_VALUE.humanReadableSize())
    }

    @Test
    fun get_near_max() {
        assertEquals("8.00 EB", (Long.MAX_VALUE - 1).humanReadableSize())
    }

    @Test
    fun get_min() {
        assertEquals("-8.00 EB", Long.MIN_VALUE.humanReadableSize())
    }

    @Test
    fun get_near_minimum() {
        assertEquals("-8.00 EB", (Long.MIN_VALUE + 2).humanReadableSize())
    }
}
