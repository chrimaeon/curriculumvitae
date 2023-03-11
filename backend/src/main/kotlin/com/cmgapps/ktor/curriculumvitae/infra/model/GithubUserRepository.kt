/*
 * Copyright (c) 2023. Christian Grach <christian.grach@cmgapps.com>
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package com.cmgapps.ktor.curriculumvitae.infra.model

import com.cmgapps.common.curriculumvitae.data.network.OssProject
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GithubUserRepository(
    val name: String,
    val private: Boolean,
    val fork: Boolean,
    val archived: Boolean,
    @SerialName("html_url")
    val htmlUrl: String,
    val description: String,
    @SerialName("tags_url")
    val tagsUrl: String,
    val topics: List<String>,
    @SerialName("stargazers_count")
    val stargazersCount: Int,
)

fun GithubUserRepository.asOssProject() = OssProject(
    name = name,
    url = htmlUrl,
    description = description,
    topics = topics,
    stars = stargazersCount,
    private = private,
    fork = fork,
    archived = archived,
)
