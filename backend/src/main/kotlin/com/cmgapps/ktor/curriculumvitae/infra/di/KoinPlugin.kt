/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.infra.di

import io.ktor.events.EventDefinition
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationStopping
import io.ktor.server.application.createApplicationPlugin
import io.ktor.server.routing.Route
import org.koin.core.Koin
import org.koin.core.context.GlobalContext
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import org.koin.core.parameter.ParametersDefinition
import org.koin.core.qualifier.Qualifier
import org.koin.logger.slf4jLogger

class KoinConfig {
    private val _modules = mutableListOf<Module>()
    val modules: List<Module> = _modules

    fun modules(vararg modules: Module) {
        _modules.addAll(modules)
    }

    var logLevel: Level = Level.INFO
}

/**
 * Workaround until Koin 3.2.0 is published
 */
val Koin = createApplicationPlugin("Koin", createConfiguration = ::KoinConfig) {
    val monitor = application.environment.monitor
    val koinApplication = startKoin(
        appDeclaration = {
            modules(pluginConfig.modules)
            slf4jLogger(pluginConfig.logLevel)
        },
    )
    monitor.raise(EventDefinition(), koinApplication)

    monitor.subscribe(ApplicationStopping) {
        monitor.raise(EventDefinition(), koinApplication)
        stopKoin()
        monitor.raise(EventDefinition(), koinApplication)
    }
}

fun getKoin(): Koin = GlobalContext.get()

inline fun <reified T : Any> Application.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = lazy { get<T>(qualifier, parameters) }

inline fun <reified T : Any> get(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = getKoin().get<T>(qualifier, parameters)

inline fun <reified T : Any> Route.inject(
    qualifier: Qualifier? = null,
    noinline parameters: ParametersDefinition? = null,
) = lazy { get<T>(qualifier, parameters) }
