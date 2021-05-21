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

package com.cmgapps.gradle;


import static org.junit.Assert.assertEquals;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.Objects;

public class ManifestTransformerTaskShould {

    private ManifestTransformerTask task;

    @Before
    public void beforeEach() {

        Project project = ProjectBuilder.builder().build();
        task = project.getTasks().create("manifestTransformer", ManifestTransformerTask.class);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Test
    public void replaceVersionCode() throws IOException, URISyntaxException {
        final File gitVersionFile = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("git/version")).toURI());
        final File androidManifest = new File(Objects.requireNonNull(this.getClass().getClassLoader().getResource("AndroidManifest.xml")).toURI());
        final File outputFile = File.createTempFile("manifestTransformer", ".tmp");

        task.getGitInfoFile().set(gitVersionFile);
        task.getAndroidManifest().set(androidManifest);
        task.getUpdatedManifest().set(outputFile);

        task.taskAction();

        assertEquals("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
            "<manifest xmlns:android=\"http://schemas.android.com/apk/res/android\"\n" +
            "    package=\"com.cmgapps.android.curriculumvitae\"\n" +
            "    android:versionCode=\"12345\"\n" +
            "    android:versionName=\"1.0\">\n" +
            "\n" +
            "    <uses-sdk\n" +
            "        android:minSdkVersion=\"26\"\n" +
            "        android:targetSdkVersion=\"30\" />\n" +
            "\n" +
            "    <application\n" +
            "        android:name=\"com.cmgapps.android.curriculumvitae.Application\"\n" +
            "        android:allowBackup=\"false\"\n" +
            "        android:icon=\"@mipmap/ic_launcher\"\n" +
            "        android:label=\"@string/app_name\"\n" +
            "        android:roundIcon=\"@mipmap/ic_launcher_round\"\n" +
            "        android:supportsRtl=\"true\"\n" +
            "        android:theme=\"@style/Theme.CurriculumVitae.Launcher\">\n" +
            "        <activity\n" +
            "            android:name=\"com.cmgapps.android.curriculumvitae.MainActivity\"\n" +
            "            android:exported=\"true\">\n" +
            "            <intent-filter>\n" +
            "                <category android:name=\"android.intent.category.LAUNCHER\" />\n" +
            "\n" +
            "                <action android:name=\"android.intent.action.MAIN\" />\n" +
            "            </intent-filter>\n" +
            "        </activity>\n" +
            "    </application>\n" +
            "\n" +
            "</manifest>\n", new String(Files.readAllBytes(outputFile.toPath())));

        outputFile.delete();
    }
}
