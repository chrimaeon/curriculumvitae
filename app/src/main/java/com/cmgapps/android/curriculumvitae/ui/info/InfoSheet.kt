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

package com.cmgapps.android.curriculumvitae.ui.info

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.WebViewDialog
import com.cmgapps.android.curriculumvitae.util.ThemedPreview
import com.cmgapps.common.curriculumvitae.CopyRightText

@Composable
fun InfoSheet(
    modifier: Modifier = Modifier,
    onOpenWebsite: (Uri) -> Unit,
) {
    var ossDialogOpen by remember { mutableStateOf(false) }
    var oflDialogOpen by remember { mutableStateOf(false) }
    val strokeColor = MaterialTheme.colorScheme.onSurface
    Column(
        modifier
            .fillMaxWidth()
            .drawBehind {
                val length = 16.dp.toPx()
                val paddingTop = 14.dp.toPx()
                drawLine(
                    strokeColor,
                    Offset(size.width / 2 - length, paddingTop),
                    Offset(size.width / 2 + length, paddingTop),
                    strokeWidth = 4.dp.toPx(),
                    cap = StrokeCap.Round,
                    alpha = 0.2f,
                )
            }
            .padding(
                WindowInsets.navigationBars
                    .add(
                        WindowInsets(
                            top = 24.dp,
                            bottom = 24.dp,
                            right = 24.dp,
                        ),
                    )
                    .asPaddingValues(),
            ),
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(start = 24.dp),
        )

        val context = LocalContext.current
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION", "kotlin:S1874")
            context.packageManager.getPackageInfo(context.packageName, 0)
        }

        Text(
            text = stringResource(
                id = R.string.version,
                packageInfo.versionName,
                PackageInfoCompat.getLongVersionCode(packageInfo),
            ),
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(Modifier.height(24.dp))
        Text(
            text = CopyRightText,
            modifier = Modifier.padding(start = 24.dp),
        )
        val uri = Uri.parse(stringResource(id = R.string.info_cmgapps_href))
        InfoTextButton(
            text = stringResource(id = R.string.info_cmgapps_link),
            onClick = { onOpenWebsite(uri) },
        )
        InfoTextButton(
            text = stringResource(id = R.string.info_oss_licenses),
            onClick = { ossDialogOpen = true },
        )
        InfoTextButton(
            text = stringResource(id = R.string.info_open_font_licenses),
            onClick = { oflDialogOpen = true },
        )
        InfoTextButton(
            text = stringResource(id = R.string.project_on_github),
            onClick = {
                onOpenWebsite(
                    Uri.parse("https://github.com/chrimaeon/curriculumvitae"),
                )
            },
        )
    }

    if (ossDialogOpen) {
        WebViewDialog(
            title = stringResource(id = R.string.info_oss_licenses),
            url = "licenses.html".asAssetFileUrl(),
            onDismissRequest = { ossDialogOpen = false },
        )
    }
    if (oflDialogOpen) {
        WebViewDialog(
            title = stringResource(id = R.string.info_open_font_licenses),
            url = "ofl-licenses.html".asAssetFileUrl(),
            onDismissRequest = { oflDialogOpen = false },
        )
    }
}

private fun String.asAssetFileUrl() = "file:///android_asset/$this"

@Composable
private fun InfoTextButton(
    text: String,
    onClick: () -> Unit,
) {
    TextButton(
        modifier = Modifier.padding(start = 16.dp),
        onClick = onClick,
    ) {
        Text(text)
    }
}

@Preview
@Composable
fun PreviewInfoSheet() {
    ThemedPreview {
        InfoSheet(onOpenWebsite = {})
    }
}
