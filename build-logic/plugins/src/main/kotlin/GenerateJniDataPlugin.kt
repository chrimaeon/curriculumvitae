/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.gradle.tasks.ExternalNativeBuildTask
import com.cmgapps.gradle.GenerateJniDataTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.register
import java.util.Locale
import javax.inject.Inject

abstract class GenerateJniDataExtension @Inject constructor(objects: ObjectFactory) {
    val source = objects.fileProperty()
}

@Suppress("UnstableApiUsage", "unused")
class GenerateJniDataPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val extension =
                extensions.create("generateJniData", GenerateJniDataExtension::class.java)

            extensions.getByType(AndroidComponentsExtension::class.java)
                .onVariants { applicationVariant ->
                    val appVariantNameCapitalized = applicationVariant.name.replaceFirstChar {
                        if (it.isLowerCase()) {
                            it.titlecase(Locale.ROOT)
                        } else {
                            it.toString()
                        }
                    }
                    val generateJniData =
                        tasks.register<GenerateJniDataTask>(
                            "generate${appVariantNameCapitalized}JniData",
                        ) {
                            source.set(extension.source)
                            outputDir.set(layout.buildDirectory.dir("generated/source/generatedJniData/${applicationVariant.name}"))
                        }

                    applicationVariant.sources.jniLibs?.addGeneratedSourceDirectory(
                        generateJniData,
                        GenerateJniDataTask::outputDir,
                    )

                    tasks.withType(ExternalNativeBuildTask::class.java) {
                        dependsOn(generateJniData)
                    }

                    applicationVariant.externalNativeBuild?.arguments?.add(
                        "-DJNI_GENERATED_DIRS=${
                            generateJniData.get().outputDir.get()
                        }",
                    )
                }
        }
    }
}
