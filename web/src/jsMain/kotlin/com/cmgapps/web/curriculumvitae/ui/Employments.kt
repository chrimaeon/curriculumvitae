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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.web.curriculumvitae.repository.EmploymentRepository
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H5
import org.jetbrains.compose.web.dom.H6
import org.jetbrains.compose.web.dom.Text

@Composable
fun Employments(repository: EmploymentRepository) {
    val (employments, setEmployments) = remember { mutableStateOf<List<Employment>?>(null) }

    LaunchedEffect(true) {
        setEmployments(repository.getEmployments())
    }

    if (employments != null) {
        for (index in employments.indices step 2) {
            Div({
                classes("row")
            }) {
                employments[index].let { employment ->
                    Div({
                        classes("col")
                    }) {
                        EmploymentCard(employment)
                    }
                }

                val employment = employments.getOrNull(index + 1)

                if (employment != null) {
                    Div({
                        classes("col-sm")
                    }) {
                        EmploymentCard(employment)
                    }
                } else {
                    Div({
                        classes("col-sm")
                    })
                }
            }
        }
    }
}

@Composable
fun EmploymentCard(employment: Employment) {
    Div({
        classes("card", "mt-3")
    }) {
        Div({
            classes("card-body")
        }) {
            H5 {
                Text(employment.employer)
            }
            H6 {
                Text(employment.workPeriod.toString())
            }
            H6 {
                Text(employment.jobTitle)
            }
        }
    }
}
