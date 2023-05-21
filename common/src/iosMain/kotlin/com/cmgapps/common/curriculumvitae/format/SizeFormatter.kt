/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.format

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import platform.Foundation.NSNumberFormatterDecimalStyle
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
        if (++ciIndex > ci.lastIndex) {
            break
        }
        i -= 10
    }
    value *= sign(this.toDouble()).toLong()
    val formatter = NSNumberFormatter().apply {
        numberStyle = NSNumberFormatterDecimalStyle
        maximumFractionDigits = 2u
        minimumFractionDigits = 2u
    }
    return "${formatter.stringFromNumber(NSNumber(value / 1024.0))} ${ci[ciIndex]}B"
}
