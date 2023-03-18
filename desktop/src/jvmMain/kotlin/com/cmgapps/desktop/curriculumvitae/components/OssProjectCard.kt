/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.desktop.curriculumvitae.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.cmgapps.common.curriculumvitae.components.StarChip
import com.cmgapps.common.curriculumvitae.data.domain.OssProject

@Composable
fun OssProjectCard(
    modifier: Modifier,
    ossProject: OssProject?,
) {
    Card(
        modifier = modifier
            .testTag("ossProjectCard${ossProject?.name ?: ""}"),
        elevation = 4.dp,
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    modifier = Modifier.weight(1F),
                    text = ossProject?.name ?: "",
                    style = MaterialTheme.typography.h5,
                )
                StarChip(
                    modifier = Modifier.padding(start = 8.dp),
                    backgroundColor = MaterialTheme.colors.primary,
                    contentColor = MaterialTheme.colors.onPrimary,
                    stars = ossProject?.stars ?: 0,
                )
            }

            Text(ossProject?.description ?: "")
        }
    }
}
