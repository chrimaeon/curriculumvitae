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

package com.cmgapps.android.curriculumvitae.ui.skills

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.components.TagCloud

@Composable
fun SkillsScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        TagCloud {
            Text(
                "Mobile Development",
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Scrum",
                style = MaterialTheme.typography.h5.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Git",
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Android",
                style = MaterialTheme.typography.h2.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Kotlin",
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "SQL",
                style = MaterialTheme.typography.h4.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                "Java",
                style = MaterialTheme.typography.h3.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Preview(
    widthDp = 320,
    heightDp = 680,
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
fun PreviewSkills() {
    MaterialTheme {
        SkillsScreen()
    }
}
