/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.android.build.api.variant.AndroidComponentsExtension
import com.cmgapps.gradle.ObfuscateEmailTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.register
import java.util.Locale
import javax.inject.Inject

abstract class ObfuscateEmailExtension @Inject constructor(objects: ObjectFactory) {
    val emailAddress: Property<String> = objects.property()
}

@Suppress("unused")
class ObfuscateEmailPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension = extensions.create("obfuscateEmail", ObfuscateEmailExtension::class.java)

            extensions.getByType(AndroidComponentsExtension::class.java)
                .onVariants { applicationVariant ->
                    val appVariantNameCapitalized = applicationVariant.name.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(Locale.ROOT)
                        } else {
                            it.toString()
                        }
                    }
                    val obfuscatedEmailTask =
                        tasks.register<ObfuscateEmailTask>(
                            "obfuscate${appVariantNameCapitalized}Email",
                        ) {
                            emailAddress.set(extension.emailAddress)
                            packageName.set("com.cmgapps.android.curriculumvitae")
                            outputDir.set(layout.buildDirectory.dir("generated/source/obfuscatedEmail/${applicationVariant.name}"))
                        }

                    @Suppress("UnstableApiUsage")
                    applicationVariant.sources.java?.addGeneratedSourceDirectory(
                        obfuscatedEmailTask,
                        ObfuscateEmailTask::outputDir,
                    )
                }
        }
    }
}
