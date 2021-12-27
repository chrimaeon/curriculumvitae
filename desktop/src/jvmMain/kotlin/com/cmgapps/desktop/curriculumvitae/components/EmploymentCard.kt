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

package com.cmgapps.desktop.curriculumvitae.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.components.AnimatedCard
import com.cmgapps.common.curriculumvitae.data.domain.Employment
import com.cmgapps.common.curriculumvitae.data.domain.asHumanReadableString
import com.cmgapps.common.curriculumvitae.resource.Res
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EmploymentCard(employment: Employment) {
    var expanded by remember { mutableStateOf(false) }
    AnimatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { expanded = !expanded },
    ) {
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = employment.employer,
                style = MaterialTheme.typography.h5

            )
            Spacer(Modifier.height(5.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                CompositionLocalProvider(
                    LocalTextStyle provides MaterialTheme.typography.body1
                ) {
                    Text(
                        modifier = Modifier.alignBy(LastBaseline),
                        text = employment.workPeriod.asHumanReadableString(),
                    )
                    Text(
                        modifier = Modifier.padding(horizontal = 2.dp).alignBy(LastBaseline),
                        text = "\u00B7",
                    )
                    val startEnd = buildString {
                        append(employment.startDate.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                        append("\u00A0")
                        append(employment.startDate.year)

                        append(" - ")

                        employment.endDate?.let { date ->
                            append(date.month.getDisplayName(TextStyle.SHORT, Locale.getDefault()))
                            append("\u00A0")
                            append(date.year)
                        } ?: append(Res.string.present)
                    }

                    Text(
                        modifier = Modifier.alignBy(LastBaseline),
                        text = startEnd,
                    )
                }
            }
            Spacer(Modifier.height(5.dp))
            Text(
                text = employment.jobTitle,
                style = MaterialTheme.typography.h6
            )

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                Column {
                    employment.description.forEach {
                        Text(
                            text = it
                        )
                    }
                }
            }
        }
    }
}
