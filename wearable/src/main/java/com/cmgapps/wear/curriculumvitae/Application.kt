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

package com.cmgapps.wear.curriculumvitae

import android.app.Application
import coil.Coil
import coil.ImageLoader
import com.cmgapps.common.curriculumvitae.infra.di.initKoin
import com.cmgapps.wear.curriculumvitae.infra.di.appModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext

class Application : Application() {

    private val imageLoader: ImageLoader by inject()

    override fun onCreate() {
        super.onCreate()
        initKoin(enableNetworkLogging = true) {
            androidContext(this@Application)
            modules(appModule)
        }
        Coil.setImageLoader(imageLoader)
    }
}
