/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.gradle

import com.cmgapps.gradle.curriculumvitae.AnsiColor
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class AnsiColorShould {

    @Test
    fun `return control sequence string for Red`() {
        assertThat(AnsiColor.Red.toString(), `is`("\u001B[0;31m"))
    }

    @Test
    fun `return control sequence string for Green`() {
        assertThat(AnsiColor.Green.toString(), `is`("\u001B[0;32m"))
    }

    @Test
    fun `return control sequence string for Yellow`() {
        assertThat(AnsiColor.Yellow.toString(), `is`("\u001B[0;33m"))
    }
}
