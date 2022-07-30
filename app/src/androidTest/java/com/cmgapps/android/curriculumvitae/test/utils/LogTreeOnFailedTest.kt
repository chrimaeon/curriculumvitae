/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.android.curriculumvitae.test.utils

import android.util.Log
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToString
import org.junit.rules.TestWatcher
import org.junit.runner.Description

internal class LogTreeOnFailedTest(
    private val composeTestRule: ComposeTestRule,
) : TestWatcher() {

    override fun failed(e: Throwable, description: Description) {
        Log.e(
            this@LogTreeOnFailedTest::class.java.simpleName,
            String.format(
                "%s %s%nwith tree%n%s",
                description.displayName,
                e.message,
                composeTestRule.onRoot().printToString(),
            ),
        )
    }
}
