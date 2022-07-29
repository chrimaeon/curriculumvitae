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
package com.cmgapps.gradle

import com.cmgapps.gradle.curriculumvitae.ManifestTransformerTask
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File

internal class ManifestTransformerTaskShould {
    private lateinit var task: ManifestTransformerTask
    private lateinit var gitFile: File

    @BeforeEach
    fun beforeEach() {
        val project = ProjectBuilder.builder().build()
        task = project.tasks.create("manifestTransformer", ManifestTransformerTask::class.java)
        gitFile = File.createTempFile("git-version", "")
    }

    @AfterEach
    fun afterEach() {
        gitFile.delete()
    }

    @Test
    fun `replace version code`() {
        val androidManifest =
            File(this.javaClass.classLoader.getResource("AndroidManifest.xml")!!.toURI())

        val gitRefCount = 1234
        val initialVersion = 1

        gitFile.writeText(gitRefCount.toString())
        val outputFile = File.createTempFile("manifestTransformer", ".tmp")

        task.gitInfoFile.set(gitFile)
        task.initialVersionCode = initialVersion
        task.androidManifest.set(androidManifest)
        task.updatedManifest.set(outputFile)
        task.taskAction()
        assertEquals(
            """
                <?xml version="1.0" encoding="utf-8"?>
                <manifest xmlns:android="http://schemas.android.com/apk/res/android"
                    package="com.cmgapps.android.curriculumvitae"
                    android:versionCode="${initialVersion + gitRefCount}"
                    android:versionName="1.0">

                    <uses-sdk
                        android:minSdkVersion="26"
                        android:targetSdkVersion="30" />

                    <application
                        android:name="com.cmgapps.android.curriculumvitae.Application"
                        android:allowBackup="false"
                        android:icon="@mipmap/ic_launcher"
                        android:label="@string/app_name"
                        android:roundIcon="@mipmap/ic_launcher_round"
                        android:supportsRtl="true"
                        android:theme="@style/Theme.CurriculumVitae.Launcher">
                        <activity
                            android:name="com.cmgapps.android.curriculumvitae.MainActivity"
                            android:exported="true">
                            <intent-filter>
                                <category android:name="android.intent.category.LAUNCHER" />

                                <action android:name="android.intent.action.MAIN" />
                            </intent-filter>
                        </activity>
                    </application>

                </manifest>

                """
                .trimIndent(),
            outputFile.readText(),
        )
        outputFile.delete()
    }
}
