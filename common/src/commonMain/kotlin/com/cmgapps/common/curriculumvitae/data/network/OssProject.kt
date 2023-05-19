/*
 * Copyright (c) 2022. Christian Grach <christian.grach@cmgapps.com>
 */

package com.cmgapps.common.curriculumvitae.data.network

import com.cmgapps.common.curriculumvitae.data.db.Ossproject
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class OssProject(
    val name: String,
    val description: String,
    val url: String,
    val topics: List<String>,
    val stars: Int,
    val private: Boolean,
    val fork: Boolean,
    val archived: Boolean,
    val pushedAt: Instant?,
)

fun Ossproject.asNetworkModel() = OssProject(
    name = name,
    url = url,
    description = description,
    topics = topics,
    stars = stars,
    private = private_,
    fork = fork,
    archived = archived,
    pushedAt = pushed_at,
)
