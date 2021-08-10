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

package com.cmgapps.web.curriculumvitae.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.cmgapps.web.curriculumvitae.data.domain.Employment
import com.cmgapps.web.curriculumvitae.repository.EmploymentRepository
import org.jetbrains.compose.web.css.margin
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Text

@Composable
fun EmploymentScreen(repo: EmploymentRepository) {

    var employments: List<Employment>? by remember { mutableStateOf(null) }

    LaunchedEffect(true) {
        employments = repo.getEmployments()
    }
    Div(
        attrs = {
            style { margin(16.px) }
        }
    ) {
        employments?.forEach {
            Text(it.toString())
        }
    }
}