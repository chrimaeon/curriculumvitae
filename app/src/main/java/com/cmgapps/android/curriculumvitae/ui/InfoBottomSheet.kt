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

package com.cmgapps.android.curriculumvitae.ui

import android.net.Uri
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.pm.PackageInfoCompat
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.components.WebViewDialog
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues

@Composable
fun InfoBottomSheet(
    onOpenWebsite: (Uri) -> Unit
) {
    var ossDialogOpen by remember { mutableStateOf(false) }
    var oflDialogOpen by remember { mutableStateOf(false) }
    Column(
        Modifier
            .fillMaxWidth()
            .padding(
                rememberInsetsPaddingValues(
                    insets = LocalWindowInsets.current.navigationBars,
                    additionalTop = 24.dp,
                    additionalEnd = 24.dp,
                    additionalBottom = 24.dp
                )
            )
    ) {
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.h5,
            modifier = Modifier.padding(start = 24.dp),
        )
        val context = LocalContext.current
        with(context.packageManager.getPackageInfo(context.packageName, 0)) {
            Text(
                text = stringResource(
                    id = R.string.version,
                    versionName,
                    PackageInfoCompat.getLongVersionCode(this)
                ),
                modifier = Modifier.padding(start = 24.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = stringResource(
                id = R.string.info_copyright,
                BuildConfig.BUILD_YEAR
            ),
            modifier = Modifier.padding(start = 24.dp),
        )
        val uri = Uri.parse(stringResource(id = R.string.info_cmgapps_href))
        InfoTextButton(
            text = stringResource(id = R.string.info_cmgapps_link),
            onClick = { onOpenWebsite(uri) },
        )
        InfoTextButton(
            text = stringResource(id = R.string.info_oss_licenses),
            onClick = { ossDialogOpen = true }
        )
        InfoTextButton(
            text = stringResource(id = R.string.info_open_font_licenses),
            onClick = { oflDialogOpen = true }
        )
        InfoTextButton(
            text = stringResource(id = R.string.project_on_github),
            onClick = {
                onOpenWebsite(
                    Uri.parse("https://github.com/chrimaeon/curriculumvitae")
                )
            }
        )
    }

    if (ossDialogOpen) {
        WebViewDialog(
            url = "licenses.html".asAssetFileUrl(),
            onDismissRequest = { ossDialogOpen = false }
        )
    }
    if (oflDialogOpen) {
        WebViewDialog(
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
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colors.primaryVariant),
        onClick = onClick,
    ) {
        Text(text)
    }
}
