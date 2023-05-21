/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.format

import kotlin.math.abs
import kotlin.math.sign

actual fun Long.humanReadableSize(): String {
    val absB = if (this == Long.MIN_VALUE) Long.MAX_VALUE else abs(this)
    if (absB < 1024) {
        return "$this B"
    }
    var value = absB
    val ci = "KMGTPE"
    var ciIndex = 0
    var i = 40
    while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
        value = value shr 10

        if (++ciIndex > ci.length - 1) {
            break
        }
        i -= 10
    }
    value *= sign(this.toDouble()).toLong()
    return "${(value / 1024.0).asDynamic().toFixed(2)} ${ci[ciIndex]}B"
}
