package com.cmgapps.android.curriculumvitae.debug

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.edit
import com.cmgapps.android.curriculumvitae.BuildConfig
import com.cmgapps.android.curriculumvitae.ui.Theme
import com.cmgapps.android.curriculumvitae.ui.Typography
import com.jakewharton.processphoenix.ProcessPhoenix

@Composable
fun DebugScreen(baseUrlPreferences: SharedPreferences) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Debug",
                        style = Typography.h6.copy(fontFamily = FontFamily.Default)
                    )
                }
            )
        },
        floatingActionButton = {
            val context = LocalContext.current
            FloatingActionButton(onClick = { ProcessPhoenix.triggerRebirth(context) }) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = "Restart App")
            }
        }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
        ) {
            BackendDropdown(baseUrlPreferences)
        }
    }
}

@Composable
private fun BackendDropdown(baseUrlPreferences: SharedPreferences) {
    val currentBaseUrl =
        baseUrlPreferences.getString(DebugActivity.BASE_URL_KEY, BuildConfig.BASE_URL)
    val baseUrls = BuildConfig.DEBUG_BASE_URLS

    var expanded by remember { mutableStateOf(false) }
    var selectedItemIndex by remember { mutableStateOf(baseUrls.indexOf(currentBaseUrl)) }

    Column {
        Header {
            Text("Base URL", style = MaterialTheme.typography.h6)
        }
        OutlinedButton(
            onClick = { expanded = true }
        ) {
            Text(text = baseUrls[selectedItemIndex])
            Spacer(Modifier.width(16.dp))
            Icon(
                imageVector = if (expanded) Icons.Filled.ArrowDropUp else Icons.Filled.ArrowDropDown,
                contentDescription = null,
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            baseUrls.forEachIndexed { index, url ->
                DropdownMenuItem(
                    onClick = {
                        baseUrlPreferences.edit(commit = true) {
                            putString(DebugActivity.BASE_URL_KEY, url)
                        }
                        selectedItemIndex = index
                        expanded = false
                    }
                ) {
                    Text(
                        text = url,
                        style = if (selectedItemIndex == index) {
                            LocalTextStyle.current.copy(color = MaterialTheme.colors.primary)
                        } else {
                            LocalTextStyle.current
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun Header(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .height(2.dp)
                .width(16.dp)
                .background(MaterialTheme.colors.primary, shape = MaterialTheme.shapes.small)
        )
        Spacer(Modifier.width(4.dp))
        content()
        Spacer(Modifier.width(4.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .width(16.dp)
                .background(MaterialTheme.colors.primary, shape = MaterialTheme.shapes.small)
        )
    }
}

// region Preview
@Preview(name = "Debug Screen light")
@Composable
fun PreviewDebugScreen() {
    Theme(darkTheme = false) {
        val prefs = LocalContext.current.getSharedPreferences("baseUrl", Context.MODE_PRIVATE)
        DebugScreen(prefs)
    }
}

@Preview(name = "Debug Screen dark")
@Composable
fun PreviewDarkDebugScreen() {
    Theme(darkTheme = true) {
        val prefs = LocalContext.current.getSharedPreferences("baseUrl", Context.MODE_PRIVATE)
        DebugScreen(prefs)
    }
}
// endregion
