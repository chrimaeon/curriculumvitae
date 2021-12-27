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

package com.cmgapps.android.curriculumvitae.debug

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import androidx.preference.DropDownPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.cmgapps.android.curriculumvitae.R
import com.cmgapps.android.curriculumvitae.databinding.ActivityDebugBinding
import com.cmgapps.common.curriculumvitae.BaseUrl
import com.cmgapps.common.curriculumvitae.DebugBaseUrls
import com.jakewharton.processphoenix.ProcessPhoenix

class DebugActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDebugBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // set actionbar first for navigation click listener not to be overridden
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.debug_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.reload -> {
                ProcessPhoenix.triggerRebirth(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun navigateUpTo(upIntent: Intent?): Boolean {
        TaskStackBuilder.create(this@DebugActivity)
            .addNextIntentWithParentStack(
                NavUtils.getParentActivityIntent(this@DebugActivity)!!
            ).startActivities()
        return true
    }

    companion object {
        const val BASE_URL_KEY = "baseUrl"
    }
}

class DebugSettingFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.debug_preferences, rootKey)

        preferenceManager.findPreference<DropDownPreference>(DebugActivity.BASE_URL_KEY)
            ?.apply {
                summaryProvider = Preference.SummaryProvider<DropDownPreference> {
                    it.entry ?: BaseUrl
                }
                setDefaultValue(BaseUrl)
                entries = DebugBaseUrls.toTypedArray()
                entryValues = DebugBaseUrls.toTypedArray()
            }
    }
}
