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

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cmgapps.web.curriculumvitae.component.Crossfade
import com.cmgapps.web.curriculumvitae.component.Route
import com.cmgapps.web.curriculumvitae.ui.EmploymentScreen
import com.cmgapps.web.curriculumvitae.ui.MaterialPage
import com.cmgapps.web.curriculumvitae.ui.ProfileScreen
import org.jetbrains.compose.web.renderComposable

fun main() {
    // val api = CvApi(HttpClient(Js), Url("http://localhost:8080"))
    // val statusRepository = StatusRepository(api)
    // val profileRepository = ProfileRepository(api)
    // val employmentRepository = EmploymentRepository(api)

    renderComposable(rootElementId = "root") {
        val (route, setRoute) = remember { mutableStateOf(Route.Profile as Route) }

        MaterialPage(
            title = "Curriculum Vitae - ${route.title}",
            setRoute = setRoute,
        ) {
            Crossfade(target = route) {
                when (it) {
                    Route.Profile -> ProfileScreen()
                    Route.Employment -> EmploymentScreen()
                }
            }
        }
    }
}
