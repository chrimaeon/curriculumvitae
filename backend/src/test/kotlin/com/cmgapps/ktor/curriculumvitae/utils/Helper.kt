/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

@file:Suppress("ktlint:filename")

package com.cmgapps.ktor.curriculumvitae.utils

import io.ktor.server.application.Application
import org.koin.core.module.Module
import org.koin.ktor.ext.getKoin

fun Application.modules(vararg block: Module) =
    getKoin().loadModules(block.asList(), allowOverride = true)
