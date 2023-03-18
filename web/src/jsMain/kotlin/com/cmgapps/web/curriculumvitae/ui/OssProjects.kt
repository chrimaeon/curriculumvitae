/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.web.curriculumvitae.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import co.touchlab.kermit.Logger
import com.cmgapps.common.curriculumvitae.data.domain.OssProject
import com.cmgapps.common.curriculumvitae.repository.OssProjectRepository
import com.cmgapps.web.curriculumvitae.AppStyle
import io.ktor.utils.io.errors.IOException
import kotlinx.browser.window
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.H6
import org.jetbrains.compose.web.dom.Img
import org.jetbrains.compose.web.dom.Text

@Composable
fun OssProjects(repository: OssProjectRepository, logger: Logger) {
    var projects: List<OssProject> by remember { mutableStateOf(emptyList()) }

    LaunchedEffect(repository) {
        try {
            projects =
                repository.getOssProjects().filter { !it.fork && !it.archived && !it.private }
        } catch (exc: IOException) {
            logger.e("Error loading projects", exc)
        }
    }

    for (index in projects.indices step 2) {
        Div(
            {
                classes("row")
            },
        ) {
            projects[index].let { project ->
                Div(
                    {
                        classes("col")
                    },
                ) {
                    OssProjectCard(project)
                }
            }

            val project = projects.getOrNull(index + 1)

            if (project != null) {
                Div(
                    {
                        classes("col-sm")
                    },
                ) {
                    OssProjectCard(project)
                }
            } else {
                Div(
                    {
                        classes("col-sm")
                    },
                )
            }
        }
    }
}

@Composable
fun OssProjectCard(ossProject: OssProject) {
    Div(
        {
            classes("card", "mt-3", "cursor-pointer")
            onClick {
                window.open(url = ossProject.url, target = "_blank")
            }
        },
    ) {
        Div(
            {
                classes("card-body")
            },
        ) {
            Div(
                { classes(AppStyle.ossProjectHeaderContainer) },
            ) {
                H4 {
                    Text(ossProject.name)
                }
                StarChip(ossProject.stars)
            }
            H6 {
                Text(ossProject.description)
            }
        }
    }
}

@Composable
fun StarChip(
    stars: Int,
) {
    Div(
        attrs = {
            classes(AppStyle.starChip)
        },
    ) {
        Img(
            src = "/star.svg",
            attrs = {
                attr("width", "24")
                attr("height", "24")
            },
        )
        Text(stars.toString())
    }
}
