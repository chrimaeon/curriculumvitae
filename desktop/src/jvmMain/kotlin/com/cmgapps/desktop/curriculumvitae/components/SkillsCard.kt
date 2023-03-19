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

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.components.TagCloud
import com.cmgapps.common.curriculumvitae.data.domain.Skill

@Composable
fun SkillsCard(skills: List<Skill>) {
    if (skills.isEmpty()) {
        return
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
    ) {
        TagCloud(
            modifier = Modifier.padding(10.dp),
        ) {
            skills.forEach { skill ->
                Text(
                    skill.name,
                    style = skill.levelAsTextStyle(),
                )
            }
        }
    }
}

@Composable
private fun Skill.levelAsTextStyle(): TextStyle = when (level) {
    1 -> MaterialTheme.typography.h6
    2 -> MaterialTheme.typography.h5
    3 -> MaterialTheme.typography.h4
    4 -> MaterialTheme.typography.h3
    5 -> MaterialTheme.typography.h2
    else -> error("level not specified")
}.copy(fontWeight = FontWeight.Bold)
