/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.common.curriculumvitae.infra.di

import kotlinx.coroutines.CoroutineDispatcher

enum class CvDispatchers {
    Default,
    IO,
}

expect val IO: CoroutineDispatcher
